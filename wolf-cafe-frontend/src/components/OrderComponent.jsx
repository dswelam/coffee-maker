import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllItems } from "../services/ItemService";
import { createOrder } from "../services/OrderService";
import { getCurrentUser, getTax } from "../services/AuthService";

const OrderComponent = () => {
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

	async function placeOrder() {
		const total = calculateTotal();
		const newErrors = {};

		if (!moneyGiven || isNaN(moneyGiven)) {
			newErrors.payment = "Enter a valid payment amount.";
			setErrors(newErrors);
			return;
		}

		if (round(moneyGiven) < total) {
			newErrors.payment = "Insufficient payment.";
			setErrors(newErrors);
			return;
		}

		if (cart.length === 0) {
			newErrors.cart = "Cart is empty.";
			setErrors(newErrors);
			return;
		}

		const current = getCurrentUser();
		if (!current || !current.username) {
			setErrors({ auth: "You must be logged in." });
			return;
		}

		const orderDto = {
			username: current.username,
			orderItems: cart.map((c) => ({
				quantity: c.qty,
				item: { id: c.item.id },
			})),
		};

		try {
			await createOrder(orderDto);
			const change = round(moneyGiven - total);
			setSuccess(`Order placed! Change due: $${change}`);
			setTimeout(() => navigate("/order"), 2000);
		} catch {
			setErrors({ api: "Error placing order." });
		}
	}

	return (
		<div className="container mt-4">
			<h2 className="text-center fw-bold mb-1">Menu</h2>
			<p className="text-center text-muted mb-4">
				Select menu items to add to your cart. Checkout below.
			</p>

			{/* MESSAGES */}
			<div className="mb-3">
				{errors.quantity && <div className="alert alert-danger">{errors.quantity}</div>}
				{errors.payment && <div className="alert alert-danger">{errors.payment}</div>}
				{errors.cart && <div className="alert alert-danger">{errors.cart}</div>}
				{errors.api && <div className="alert alert-danger">{errors.api}</div>}
				{errors.auth && <div className="alert alert-danger">{errors.auth}</div>}
				{success && <div className="alert alert-success">{success}</div>}
			</div>

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
						<li key={index} className="list-group-item">
							{c.item.name} × {c.qty} — ${round(c.item.price * c.qty)}
						</li>
					))}
				</ul>
			)}

			{/* BOTTOM ROW: TOTAL | TIP | PAYMENT */}
			<div className="row g-4 mb-4">

				{/* TOTALS */}
				<div className="col-md-4">
					<h4>Total</h4>
					<div><strong>Tax</strong>: ${calculateTax()} ({(taxRate * 100).toFixed(2)}%)</div>
					<div>Subtotal: ${calculateSubtotal()}</div>
					<div>Tip: ${calculateTip()}</div>
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
						value={customTip}
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
			</div>

			<button className="btn btn-primary w-100 mb-3" onClick={placeOrder}>
				Place Order
			</button>
		</div>
	);
};

export default OrderComponent;
