// src/services/OrderService.js
import axios from 'axios';
import { getToken } from './AuthService';

const ORDER_API_BASE_URL = 'http://localhost:8080/api/orders';

const authHeader = () => {
  const token = getToken();
  if (!token) return {};
  return { Authorization: 'Bearer ' + token };
};

export const createOrder = (orderDto) => {
  return axios.post(ORDER_API_BASE_URL, orderDto, {
    headers: {
      'Content-Type': 'application/json',
      ...authHeader()
    }
  });
};

// order queue listing 
export const OrderQueue = () => axios.get(ORDER_API_BASE_URL + '/' + 'queue')

export function updateOrderStatus(orderId, status) {
  return axios.put(`/api/orders/${orderId}/status`, { status });
}

export function prepareOrder(id) {
  return axios.put(`/api/orders/${id}/prepare`);
}

export function markReady(id) {
  return axios.put(`/api/orders/${id}/ready`);
}

export function fulfillOrder(id) {
  return axios.put(`/api/orders/${id}/fulfill`);
}

export function cancelOrder(id) {
  return axios.put(`/api/orders/${id}/cancel`);
}
