import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { OrderQueue, prepareOrder, markReady, fulfillOrder, cancelOrder } from "../services/OrderService"


const OrderQueueComponent = () => {
  const navigate = useNavigate()
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const [orders, setOrders] = useState([])

  useEffect(() => {
  	listOrders()
  }, [])

  function listOrders() {
  	OrderQueue().then((response) => {
  		setOrders(response.data)
  	}).catch(error => {
  		console.error(error)
  		setErrorMsg('Failed to load orders.');
  	})
  }
  
  useEffect(() => {
  	if (successMsg) {
  		const timer = setTimeout(() => setSuccessMsg(''), 3000); // clears after 3s
  		return () => clearTimeout(timer);
  	}
  }, [successMsg]);

  useEffect(() => {
  	if (errorMsg) {
  		const timer = setTimeout(() => setError(''), 3000); // clears after 3s
  		return () => clearTimeout(timer);
  	}
  }, [errorMsg]);
  
  
  async function handleStatusSelect(orderId, newStatus) {
    try {
      if (newStatus === "IN_PROGRESS") {
        await prepareOrder(orderId);
      } else if (newStatus === "READY") {
        await markReady(orderId);
      } else if (newStatus === "FULFILLED") {
        await fulfillOrder(orderId);
      } else if (newStatus === "CANCELLED") {
        await cancelOrder(orderId);
      } else {
        return; // "PLACED" can't be set manually
      }

      setSuccessMsg("Order status updated!");
      listOrders();
    } catch (err) {
      console.error(err);
      setErrorMsg("Unable to update order status.");
    }
  }
  
  function getValidTransitions(status) {
    switch (status) {
      case "PLACED":
        return ["IN_PROGRESS", "CANCELLED"];
      case "IN_PROGRESS":
        return ["READY"];
      case "READY":
        return ["FULFILLED"];
      default:
        return []; // no transitions allowed
    }
  }


  return (
    <div className="container" style={{ paddingTop: '40px' }}>
      <h2 className="fw-bold text-center mb-4">Order Queue</h2>

      {errorMsg && <div className="alert alert-danger text-center">{errorMsg}</div>}
      {successMsg && <div className="alert alert-success text-center">{successMsg}</div>}

      {orders.length === 0 ? (
        <p className="text-center">No orders found.</p>
      ) : (
        <div className="d-flex flex-wrap justify-content-center gap-4">
          {orders.map((order) => (
            <div
              key={order.id}
              className="card shadow-lg p-4"
              style={{
                width: "28rem",
                borderRadius: "1rem",
                backgroundColor: "#fff",
                display: "flex",
                flexDirection: "row",
                alignItems: "center",
                transform: "scale(0.98)"
              }}
            >
              <div style={{ flexGrow: 1 }}>
                <h4 className="fw-bold mb-3" style={{ color: "#333" }}>
                  Order #{order.id}
                </h4>

                <p className="mb-2">
                  <span className="fw-semibold">Customer:</span>{" "}
                  {order.customer?.name || "Unknown"}
                </p>

				<p className="mb-0">
				  <span className="fw-semibold">Status:</span>{" "}
				  <span
				    className={`badge ${
				      order.status === "PENDING"
				        ? "bg-warning text-dark"
				        : order.status === "COMPLETED"
				        ? "bg-success"
				        : "bg-secondary"
				    }`}
				    style={{ fontSize: "1rem" }}
				  >
				    {order.status}
				  </span>
				</p>

				{getValidTransitions(order.status).length > 0 && (
				  <select
				    className="form-select mt-3"
				    style={{ maxWidth: "200px" }}
				    defaultValue=""
				    onChange={(e) => handleStatusSelect(order.id, e.target.value)}
				  >
				    <option value="" disabled>
				      Update Status…
				    </option>

				    {getValidTransitions(order.status).map((next) => (
				      <option key={next} value={next}>
				        {next}
				      </option>
				    ))}
				  </select>
				)}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default OrderQueueComponent
