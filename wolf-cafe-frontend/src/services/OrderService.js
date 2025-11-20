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

export const updateOrder = (id, orderDto) => {
  return axios.put(`${ORDER_API_BASE_URL}/${id}`, orderDto, {
    headers: {
      "Content-Type": "application/json",
      ...authHeader(),
    },
  });
};

export const listMyOrders = () => {
  return axios.get(`${ORDER_API_BASE_URL}/my-orders`, {
    headers: {
      ...authHeader(),
    },
  });
};


export const fulfillOrder = (orderId) => {
  return axios.put(`${ORDER_API_BASE_URL}/${orderId}/fulfill`, null, {
    headers: {
      ...authHeader(),
    },
  });
};


export const cancelOrder = (orderId) => {
  return axios.put(`${ORDER_API_BASE_URL}/${orderId}/cancel`, null, {
    headers: {
      ...authHeader(),
    },
  });
};