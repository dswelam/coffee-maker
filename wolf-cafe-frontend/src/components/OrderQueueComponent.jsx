import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { OrderQueue } from "../services/OrderService"


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
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default OrderQueueComponent
