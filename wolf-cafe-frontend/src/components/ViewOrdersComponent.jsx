import React, { useEffect, useState } from "react";
import { listMyOrders } from "../services/OrderService";
import OrderCard from "./OrderCard";

const ViewOrdersComponent = () => {
	const [orders, setOrders] = useState([]);
	const [loading, setLoading] = useState(true);
	const [expandedOrders, setExpandedOrders] = useState({});

	const fetchOrders = () => {
		setLoading(true);
		listMyOrders()
			.then((res) => {
				setOrders(res.data);
				setLoading(false);
			})
			.catch((err) => {
				console.error(err);
				setLoading(false);
			});
	};

	useEffect(() => {
		fetchOrders();
	}, []);

	if (loading) return <p className="text-center mt-6">Loading your orders...</p>;

	// FILTER SECTIONS 
	const placedOrders = orders.filter((o) => o.status === "PLACED");

	const activeOrders = orders.filter(
		(o) => o.status === "READY" || o.status === "IN_PROGRESS"
	);

	const completedOrders = orders.filter(
		(o) => o.status === "FULFILLED" || o.status === "CANCELLED"
	);

	const toggleExpand = (id) => {
		setExpandedOrders((prev) => ({ ...prev, [id]: !prev[id] }));
	};

	// renderOrders now returns a grid of smaller cards (like order queue)
	const renderOrders = (list) => {
		return (
			<div className="d-flex flex-wrap gap-4">
				{list.map((order) => (
					<div
						key={order.id}
						className="card shadow p-4 mb-4"
						style={{ width: "26rem", borderRadius: "1rem", background: "#fff" }}
					>
						<h5 className="fw-bold mb-2">Order #{order.id}</h5>

						{/* OrderCard contains status + action buttons */}
						<OrderCard order={order} refresh={fetchOrders} />

						<button
							className="btn btn-outline-secondary mt-2"
							onClick={() => toggleExpand(order.id)}
						>
							{expandedOrders[order.id] ? "Hide Items" : "Show Items"}
						</button>

						{expandedOrders[order.id] && (
							<ul className="list-group mt-2">
								{order.orderItems.map((oi) => (
									<li key={oi.id} className="list-group-item">
										{oi.item.name} × {oi.quantity}
									</li>
								))}
							</ul>
						)}
					</div>
				))}
			</div>
		);
	};

	return (
		<div className="container" style={{ paddingTop: "40px" }}>
			<h1 className="fw-bold text-center mb-2">My Orders</h1>
			<p className="text-center text-muted mb-5">
				You can only edit or cancel orders that are still placed.
			</p>

			{/* PLACED */}
			<div className="mb-5">
				<h3 className="fw-bold mb-3">Orders You Just Placed</h3>
				{placedOrders.length === 0 ? (
					<p className="text-muted">No placed orders.</p>
				) : (
					renderOrders(placedOrders)
				)}
			</div>

			{/* ACTIVE */}
			<div className="mb-5">
				<h3 className="fw-bold mb-3">Active Orders</h3>
				{activeOrders.length === 0 ? (
					<p className="text-muted">No active orders right now.</p>
				) : (
					renderOrders(activeOrders)
				)}
			</div>

			{/* COMPLETED */}
			<div className="mb-5">
				<h3 className="fw-bold mb-3">Completed or Cancelled</h3>
				{completedOrders.length === 0 ? (
					<p className="text-muted">No completed orders.</p>
				) : (
					renderOrders(completedOrders)
				)}
			</div>
		</div>
	);
};

export default ViewOrdersComponent;
