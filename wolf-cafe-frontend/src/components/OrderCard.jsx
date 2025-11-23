import React from "react";
import { useNavigate } from "react-router-dom";
import { fulfillOrder, cancelOrder } from "../services/OrderService";

const OrderCard = ({ order, refresh }) => {
	const navigate = useNavigate();

	const handleFulfill = () => {
		fulfillOrder(order.id)
			.then(() => {
				alert("Order picked up!");
				refresh();
			})
			.catch((err) => {
				alert(err.response?.data?.message || "Unable to pick up order.");
			});
	};

	const handleCancel = () => {
		cancelOrder(order.id)
			.then(() => {
				alert("Order cancelled.");
				refresh();
			})
			.catch((err) => {
				alert(err.response?.data?.message || "Unable to cancel order.");
			});
	};

	const handleEdit = () => {
		navigate(`/edit-order/${order.id}`);
	};

	const getStatusColor = (status) => {
		switch (status) {
			case "PLACED":
				return "bg-secondary";
			case "READY":
				return "bg-success";
			case "FULFILLED":
				return "bg-primary";
			case "CANCELLED":
				return "bg-danger";
			default:
				return "bg-dark";
		}
	};

	return (
		<div className="p-4 mb-3 border rounded shadow-sm bg-white">
			<h2 className="text-lg fw-bold mb-2">Order #{order.id}</h2>

			{/* STATUS ROW WITH BADGE */}
			<p className="mb-3">
				<span className="fw-semibold">Status:</span>{" "}
				<span className={`badge ${getStatusColor(order.status)}`}>
					{order.status}
				</span>
			</p>

			<div className="d-flex flex-wrap" style={{ gap: "8px" }}>
				{/* PICK UP ORDER */}
				{order.status === "READY" && (
					<button onClick={handleFulfill} className="btn btn-success">
						Pick Up Order
					</button>
				)}

				{/* EDIT ORDER */}
				{order.status === "PLACED" && (
					<button onClick={handleEdit} className="btn btn-primary">
						Edit Order
					</button>
				)}

				{/* CANCEL ORDER */}
				{order.status === "PLACED" && (
					<button onClick={handleCancel} className="btn btn-danger">
						Cancel Order
					</button>
				)}
			</div>
		</div>
	);
};

export default OrderCard;
