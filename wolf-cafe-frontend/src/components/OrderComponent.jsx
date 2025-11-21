import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getAllItems } from "../services/ItemService";
import { createOrder, getOrderById, updateOrder } from "../services/OrderService";
import { getCurrentUser, getTax } from "../services/AuthService";

const OrderComponent = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [items, setItems] = useState([]);
  const [cart, setCart] = useState([]);
  const [tipPercent, setTipPercent] = useState(0);
  const [customTip, setCustomTip] = useState("");
  const [moneyGiven, setMoneyGiven] = useState("");
  const [errors, setErrors] = useState({});
  const [success, setSuccess] = useState("");
  const [taxRate, setTaxRate] = useState(0);

	const round = (n) => Number(Number(n).toFixed(2));

	// fetch menu items
	useEffect(() => {
		getAllItems()
			.then((res) => setItems(res.data))
			.catch(() =>
				setErrors((prev) => ({ ...prev, api: "Could not load menu items." }))
			);

		// fetch tax rate from API
		getTax()
			.then((res) => {
				// res.data is the number from your API, e.g., 5.6
				const rate = res.data ? res.data / 100 : 0.0725; // convert 5.6 → 0.056
				setTaxRate(rate);
			})
			.catch(() => setTaxRate(0.0725));
	}, []);
	
	// fetch existing order if editing
	// fetch existing order if editing
	useEffect(() => {
	  const fetchOrder = async () => {
	    if (!orderId) return;
	    try {
	      // Wait until items are loaded
	      if (!items.length) return;

	      const res = await getOrderById(orderId);
	      const order = res.data;

	      // map orderItems to cart
	      const mappedCart = order.orderItems.map((oi) => {
	        // match menu item by ID (coerce to number just in case)
	        const menuItem = items.find((i) => Number(i.id) === Number(oi.item.id));
	        return {
	          item: menuItem || oi.item,
	          qty: oi.quantity,
	        };
	      });

	      setCart(mappedCart);
	    } catch (err) {
	      console.error("Error loading order:", err);
	      setErrors( "Could not load order for editing." );
	    }
	  };

	  fetchOrder();
	}, [orderId, items]);



	// auto-hide messages
	useEffect(() => {
		if (success || Object.values(errors).some(Boolean)) {
			const t = setTimeout(() => {
				setSuccess("");
				setErrors({});
			}, 3000);
			return () => clearTimeout(t);
		}
	}, [success, errors]);

	function removeFromCart(index) {
	  setCart((prevCart) => prevCart.filter((_, i) => i !== index));
	}

	function addToCart(item, qty) {
		const q = Number(qty);
		if (q < 0 || isNaN(q)) {
			setErrors({ quantity: "Quantity must be zero or more." });
			return;
		}
		setErrors((prev) => ({ ...prev, quantity: null }));
		if (q === 0) return; // allow display 0 but don't add zero qty
		setCart([...cart, { item, qty: q }]);
	}

	const calculateSubtotal = () =>
		round(cart.reduce((sum, c) => sum + c.item.price * c.qty, 0));
	const calculateTax = () => round(calculateSubtotal() * taxRate);
	const calculateTip = () => {
		if (tipPercent > 0) return round(calculateSubtotal() * tipPercent);
		if (customTip) return round(customTip);
		return 0;
	};
	const calculateTotal = () =>
		round(calculateSubtotal() + calculateTax() + calculateTip());

	useEffect(() => {
		if (errors.payment && moneyGiven) {
			if (round(moneyGiven) >= calculateTotal()) {
				setErrors((prev) => ({ ...prev, payment: null }));
			}
		}
	}, [moneyGiven, cart, tipPercent, customTip]);

	async function submitOrder() {
		// check that cart has at least one item
		if (cart.length === 0) {
		    setErrors((prev) => ({ ...prev, cart: "Cart cannot be empty." }));
		    return;
		}
		// validate payment
		const total = calculateTotal();
		if (!moneyGiven) {
		  setErrors((prev) => ({ ...prev, payment: "Payment cannot be empty." }));
		  return;
		}
		if (Number(moneyGiven) < total) {
		  setErrors((prev) => ({ ...prev, payment: "Payment must be at least total amount." }));
		  return;
		}
		
		const orderDto = {
	    orderItems: cart.map(c => ({
	      quantity: c.qty,
	      item: { id: c.item.id }
	    }))
	  };

	  try {
	    if (orderId) {
	      // EDIT existing order
	      await updateOrder(orderId, orderDto);
	      setSuccess("Order updated successfully!");
		  setTimeout(() => navigate("/my-orders"), 2000)
	    } else {
	      // NEW order
	      await createOrder(orderDto);
	      setSuccess("Order placed successfully!");
		    setCart([]);
			items.forEach(item => {
			  const el = document.getElementById(`qty-${item.id}`);
			  if (el) el.value = 0;
			});

		    setMoneyGiven("");
		    setTipPercent(0);
		    setCustomTip("");
	    }
	    setTimeout(() => navigate("/order"), 2000);
	  } catch {
	    setErrors({ api: "Error submitting order." });
	  }
	}

	return (
		<div className="container mt-4">
			<h2 className="text-center fw-bold mb-1">Menu</h2>
			<p className="text-center text-muted mb-4">
				Select menu items to add to your cart. Checkout below.
			</p>

			{/* MENU GRID */}
			<div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-3 mb-4">
				{items.map((item) => (
					<div key={item.id} className="col">
						<div className="card p-3 shadow-sm h-100 text-center d-flex flex-column justify-content-between">

							<div>
								<h5 className="fw-bold">{item.name}</h5>
								<p className="text-muted">${item.price.toFixed(2)}</p>
							</div>

							{/* QTY CONTROLS */}
							<div className="d-flex justify-content-center align-items-center mt-3" style={{ gap: "8px" }}>
								<button
									className="btn btn-outline-secondary"
									style={{ width: "36px", height: "36px" }}
									onClick={() => {
										const el = document.getElementById(`qty-${item.id}`);
										const val = Number(el.value || 0);
										el.value = Math.max(0, val - 1);
									}}
								>
									-
								</button>

								<input
									id={`qty-${item.id}`}
									type="number"
									min="0"
									defaultValue="0"
									className="form-control text-center"
									style={{ maxWidth: "70px" }}
								/>

								<button
									className="btn btn-outline-secondary"
									style={{ width: "36px", height: "36px" }}
									onClick={() => {
										const el = document.getElementById(`qty-${item.id}`);
										const val = Number(el.value || 0);
										el.value = val + 1;
									}}
								>
									+
								</button>
							</div>

							<button
								className="btn btn-success w-100 mt-3"
								onClick={() =>
									addToCart(item, document.getElementById(`qty-${item.id}`).value)
								}
							>
								Add to Cart
							</button>
						</div>
					</div>
				))}
			</div>

			{/* CART */}
			<h4 className="fw-bold">Your Cart</h4>
			{cart.length === 0 ? (
				<div className="text-muted mb-4">No items yet.</div>
			) : (
				<ul className="list-group mb-4">
				  {cart.map((c, index) => (
				    <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
				      <span>
				        {c.item.name} × {c.qty} — ${round(c.item.price * c.qty)}
				      </span>
				      <button
				        className="btn btn-sm btn-danger"
				        onClick={() => removeFromCart(index)}
				      >
				        Delete
				      </button>
				    </li>
				  ))}
				</ul>

			)}

			{/* BOTTOM ROW: TOTAL | TIP | PAYMENT */}
			<div className="row g-4 mb-4">

				{/* TOTALS */}
				<div className="col-md-4">
					<h4>Total</h4>
					<div>
					  Tax: ({(taxRate*100).toFixed(2)}%) ${calculateTax()}
					</div>
					<div>Subtotal: ${calculateSubtotal()}</div>
					<div>
					  Tip: {tipPercent > 0 && `(${(tipPercent*100).toFixed(0)}%) `}${calculateTip()}
					</div>

					<hr />
					<div className="fw-bold fs-5">Total: ${calculateTotal()}</div>
				</div>

				{/* TIP */}
				<div className="col-md-4">
					<div className="d-flex justify-content-between align-items-center mb-2">
						<h4 className="mb-0">Tip</h4>
						<div>
						<button
							className="btn btn-outline-primary btn-sm me-1"
							onClick={() => {
								setTipPercent(0.15);
								setCustomTip("" + round(calculateSubtotal() * 0.15));
							}}
						>
							15%
						</button>
						<button
							className="btn btn-outline-primary btn-sm me-1"
							onClick={() => {
								setTipPercent(0.2);
								setCustomTip("" + round(calculateSubtotal() * 0.2));
							}}
						>
							20%
						</button>
						<button
							className="btn btn-outline-primary btn-sm"
							onClick={() => {
								setTipPercent(0.25);
								setCustomTip("" + round(calculateSubtotal() * 0.25));
							}}
						>
							25%
						</button>

						</div>
					</div>

					<input
						className="form-control"
						value={tipPercent * 100}
						placeholder="Custom Tip ($)"
						onChange={(e) => {
							setCustomTip(e.target.value);
							setTipPercent(0);
						}}
					/>

				</div>

				{/* PAYMENT */}
				<div className="col-md-4">
					<h4 className="mb-2">Payment</h4>
					<input
						type="number"
						className="form-control"
						value={moneyGiven}
						placeholder="Amount Given"
						onChange={(e) => {
							setMoneyGiven(e.target.value);
							setErrors((prev) => ({ ...prev, payment: null }));
						}}
					/>
				</div>
				{/* MESSAGES */}
				<div className="mb-3">
					{errors.quantity && <div className="alert alert-danger">{errors.quantity}</div>}
					{errors.payment && <div className="alert alert-danger">{errors.payment}</div>}
					{errors.cart && <div className="alert alert-danger">{errors.cart}</div>}
					{errors.api && <div className="alert alert-danger">{errors.api}</div>}
					{errors.auth && <div className="alert alert-danger">{errors.auth}</div>}
					{success && <div className="alert alert-success">{success}</div>}
				</div>
			</div>

			<button className="btn btn-primary w-100 mb-3" onClick={submitOrder}>
			  {orderId ? "Update Order" : "Place Order"}
			</button>

		</div>
	);
};

export default OrderComponent;
