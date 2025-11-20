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
export const getOrdersByStatus = (stat) =>
  axios.get(`${ORDER_API_BASE_URL}/queue?status=${stat}`, { headers: authHeader() });

export const prepareOrder = (id) =>
  axios.put(`${ORDER_API_BASE_URL}/${id}/prepare`, {}, { headers: authHeader() });

export const markReady = (id) =>
  axios.put(`${ORDER_API_BASE_URL}/${id}/ready`, {}, { headers: authHeader() });

export const fulfillOrder = (id) =>
  axios.put(`${ORDER_API_BASE_URL}/${id}/fulfill`, {}, { headers: authHeader() });

export const cancelOrder = (id) =>
  axios.put(`${BASE}/${id}/cancel`, {}, { headers: authHeader() });

