import React, { useEffect, useState } from "react";
import { listMyOrders } from "../services/OrderService";
import OrderCard from "./OrderCard";

const ViewOrdersComponent = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

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

  return (
    <div className="max-w-3xl mx-auto mt-8">
      <h1 className="text-2xl font-bold mb-5">My Orders</h1>

      {orders.length === 0 ? (
        <p>You don't have any orders yet.</p>
      ) : (
        orders.map((order) => (
          <OrderCard key={order.id} order={order} refresh={fetchOrders} />
        ))
      )}
    </div>
  );
};

export default ViewOrdersComponent;
