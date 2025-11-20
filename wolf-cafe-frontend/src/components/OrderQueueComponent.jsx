import React, { useEffect, useState } from "react";
import {
  getOrdersByStatus,
  prepareOrderByStaff,
  markReadyByStaff,
  fulfillOrderByStaff,
  cancelOrder,
} from "../services/OrderService";

const STATUS_ORDER = [
  "PLACED",
  "IN_PROGRESS",
  "READY",
  "FULFILLED",
  "CANCELLED",
];

const OrderQueueComponent = () => {
  const [ordersByGroup, setOrdersByGroup] = useState({});
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [expandedOrders, setExpandedOrders] = useState({}); // track which orders are expanded

  useEffect(() => {
    loadAllGroups();
  }, []);

  async function loadAllGroups() {
    try {
      const results = {};
      for (let status of STATUS_ORDER) {
        const res = await getOrdersByStatus(status);
        results[status] = res.data;
      }
      setOrdersByGroup(results);
    } catch (err) {
      console.error(err);
      setErrorMsg("Couldn't load queue.");
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
        return [];
    }
  }

  function getStatusColor(status) {
    switch (status) {
      case "PLACED":
        return "bg-secondary"; // grey
      case "IN_PROGRESS":
        return "bg-warning text-dark"; // yellow
      case "READY":
        return "bg-success"; // green
      case "FULFILLED":
        return "bg-primary"; // blue
      case "CANCELLED":
        return "bg-danger"; // red
      default:
        return "bg-dark";
    }
  }

  async function updateStatus(id, next) {
    try {
      if (next === "IN_PROGRESS") await prepareOrderByStaff(id);
      else if (next === "READY") await markReadyByStaff(id);
      else if (next === "FULFILLED") await fulfillOrderByStaff(id);
      else if (next === "CANCELLED") await cancelOrder(id);

      setSuccessMsg("Order updated successfully.");
      loadAllGroups();
    } catch (err) {
      console.error(err);
      setErrorMsg("Failed to update order.");
    }
  }

  const toggleExpand = (orderId) => {
    setExpandedOrders((prev) => ({
      ...prev,
      [orderId]: !prev[orderId],
    }));
  };

  return (
    <div className="container" style={{ paddingTop: "40px" }}>
      <h2 className="fw-bold text-center mb-4">Order Queue</h2>

      {errorMsg && <div className="alert alert-danger text-center">{errorMsg}</div>}
      {successMsg && <div className="alert alert-success text-center">{successMsg}</div>}

      {STATUS_ORDER.map((status) => {
        const list = ordersByGroup[status] || [];
        if (list.length === 0) return null;

        return (
          <div key={status} className="mb-5">
            <h3 className="fw-bold mb-3">{status.replace("_", " ")}</h3>

            <div className="d-flex flex-wrap gap-4">
              {list.map((order) => (
                <div
                  key={order.id}
                  className="card shadow p-4"
                  style={{
                    width: "26rem",
                    borderRadius: "1rem",
                    background: "#fff",
                  }}
                >
                  <h5 className="fw-bold">Order #{order.id}</h5>

                  <p className="mb-0">
                    <span className="fw-semibold">Customer:</span>{" "}
                    {order.customer?.name || "Unknown"}
                  </p>

                  <p className="mt-2 mb-0">
                    <span className="fw-semibold">Status:</span>{" "}
                    <span className={`badge ${getStatusColor(order.status)}`}>
                      {order.status}
                    </span>
                  </p>

                  {getValidTransitions(order.status).length > 0 && (
                    <select
                      className="form-select mt-3"
                      defaultValue=""
                      style={{ maxWidth: "200px" }}
                      onChange={(e) => updateStatus(order.id, e.target.value)}
                    >
                      <option value="" disabled>
                        Update Status
                      </option>
                      {getValidTransitions(order.status).map((s) => (
                        <option key={s} value={s}>
                          {s}
                        </option>
                      ))}
                    </select>
                  )}

                  {/* Collapsible items list */}
                  <button
                    className="btn btn-outline-secondary mt-3"
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
          </div>
        );
      })}
    </div>
  );
};

export default OrderQueueComponent;
