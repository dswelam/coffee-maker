import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { getAllItems } from "../services/ItemService"
import { createOrder } from "../services/OrderService"
import { getCurrentUser } from "../services/AuthService"
import { getUserById } from "../services/UserService"

const OrderComponent = () => {
  const navigate = useNavigate()

  const [items, setItems] = useState([])
  const [cart, setCart] = useState([])
  const [tipPercent, setTipPercent] = useState(0)
  const [customTip, setCustomTip] = useState("")
  const [moneyGiven, setMoneyGiven] = useState("")
  const [errors, setErrors] = useState({})
  const [success, setSuccess] = useState("")

  const TAX_RATE = 0.0725
  const round = (n) => Number(Number(n).toFixed(2))

  useEffect(() => {
    getAllItems()
      .then((res) => setItems(res.data))
      .catch(() =>
        setErrors((prev) => ({
          ...prev,
          api: "Could not load menu items."
        }))
      )
  }, [])

  /** Add item to cart **/
  function addToCart(item, qty) {
    const q = Number(qty)
    if (!q || q <= 0) {
      setErrors({ quantity: "Quantity must be a positive number." })
      return
    }

    setErrors((prev) => ({ ...prev, quantity: null }))
    setCart([...cart, { item, qty: q }])
  }

  /** CART CALCULATIONS **/
  const calculateSubtotal = () =>
    round(cart.reduce((sum, c) => sum + c.item.price * c.qty, 0))

  const calculateTax = () => round(calculateSubtotal() * TAX_RATE)

  const calculateTip = () => {
    if (tipPercent > 0) return round(calculateSubtotal() * tipPercent)
    if (customTip) return round(customTip)
    return 0
  }

  const calculateTotal = () =>
    round(calculateSubtotal() + calculateTax() + calculateTip())

  /** Clear payment error when user types **/
  useEffect(() => {
    if (errors.payment && moneyGiven) {
      if (round(moneyGiven) >= calculateTotal()) {
        setErrors((prev) => ({ ...prev, payment: null }))
      }
    }
  }, [moneyGiven, cart, tipPercent, customTip])

  /** Build DTO & POST **/
  async function placeOrder() {
    const newErrors = {}
    const total = calculateTotal()

    // Payment validation
    if (!moneyGiven || isNaN(moneyGiven)) {
      newErrors.payment = "Enter a valid payment amount."
      setErrors(newErrors)
      return
    }

    if (round(moneyGiven) < total) {
      newErrors.payment = "Insufficient payment."
      setErrors(newErrors)
      return
    }

    if (cart.length === 0) {
      newErrors.cart = "Cart is empty."
      setErrors(newErrors)
      return
    }

    const current = getCurrentUser()
    if (!current || !current.id) {
      setErrors({ auth: "You must be logged in." })
      return
    }

    let fullUser
    try {
      const res = await getUserById(current.id)
      fullUser = res.data
    } catch (err) {
      setErrors({ api: "Could not load user profile." })
      return
    }

    // Build order DTO exactly as backend expects
    const orderDto = {
      customer: fullUser,
      preparedBy: fullUser, // temporary assumption
      orderItems: cart.map((c) => ({
        quantity: c.qty,
        item: {
          id: c.item.id,
          name: c.item.name,
          description: c.item.description,
          price: c.item.price,
          ingredients: c.item.ingredients
        }
      }))
    }

    try {
      await createOrder(orderDto)

      const change = round(moneyGiven - total)

      setSuccess(`Order placed! Change due: $${change}`)

      setTimeout(() => navigate("/"), 2000)
    } catch (err) {
      console.error(err)
      setErrors({ api: "Error placing order." })
    }
  }

  return (
    <div className="container mt-5">
      <h2 className="text-center fw-bold mb-4">Build Your Order</h2>

      {/* ERRORS */}
      {errors.quantity && <div className="alert alert-danger">{errors.quantity}</div>}
      {errors.payment && <div className="alert alert-danger">{errors.payment}</div>}
      {errors.cart && <div className="alert alert-danger">{errors.cart}</div>}
      {errors.api && <div className="alert alert-danger">{errors.api}</div>}
      {errors.auth && <div className="alert alert-danger">{errors.auth}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      {/* MENU */}
      <h4>Menu</h4>
      <ul className="list-group mb-4">
        {items.map((item) => (
          <li className="list-group-item d-flex justify-content-between" key={item.id}>
            <span>
              {item.name} — ${item.price.toFixed(2)}
            </span>
            <div>
              <input
                id={`qty-${item.id}`}
                type="number"
                min="1"
                placeholder="Qty"
                className="form-control d-inline-block"
                style={{ width: "80px" }}
              />
              <button
                className="btn btn-success ms-2"
                onClick={() =>
                  addToCart(item, document.getElementById(`qty-${item.id}`).value)
                }
              >
                Add
              </button>
            </div>
          </li>
        ))}
      </ul>

      {/* CART */}
      <h4>Your Cart</h4>
      {cart.length === 0 ? (
        <div className="text-muted mb-4">No items yet.</div>
      ) : (
        <ul className="list-group mb-4">
          {cart.map((c, index) => (
            <li className="list-group-item" key={index}>
              {c.item.name} × {c.qty} — ${round(c.item.price * c.qty)}
            </li>
          ))}
        </ul>
      )}

      {/* TIP */}
      <h4>Tip</h4>
      <div className="d-flex mb-3">
        <button
          className="btn btn-outline-primary me-2"
          onClick={() => {
            setTipPercent(0.15)
            setCustomTip("")
          }}
        >
          15%
        </button>
        <button
          className="btn btn-outline-primary me-2"
          onClick={() => {
            setTipPercent(0.2)
            setCustomTip("")
          }}
        >
          20%
        </button>
        <button
          className="btn btn-outline-primary me-2"
          onClick={() => {
            setTipPercent(0.25)
            setCustomTip("")
          }}
        >
          25%
        </button>
      </div>

      <input
        className="form-control mb-4"
        value={customTip}
        placeholder="Custom Tip ($)"
        onChange={(e) => {
          setCustomTip(e.target.value)
          setTipPercent(0)
        }}
      />

      {/* PAYMENT */}
      <h4>Payment</h4>
      <input
        type="number"
        className="form-control mb-4"
        value={moneyGiven}
        placeholder="Amount Given"
        onChange={(e) => {
          setMoneyGiven(e.target.value)
          setErrors((prev) => ({ ...prev, payment: null }))
        }}
      />

      {/* TOTAL */}
      <h4>Total</h4>
      <div className="mb-4">
        Subtotal: ${calculateSubtotal()} <br />
        Tax: ${calculateTax()} <br />
        Tip: ${calculateTip()} <br />
        <strong>Total: ${calculateTotal()}</strong>
      </div>

      <button className="btn btn-primary w-100" onClick={placeOrder}>
        Place Order
      </button>
    </div>
  )
}

export default OrderComponent
