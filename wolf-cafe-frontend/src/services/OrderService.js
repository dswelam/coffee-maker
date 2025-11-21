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

export const prepareOrderByStaff  = (id) =>
  axios.put(`${ORDER_API_BASE_URL}/${id}/prepare`, {}, { headers: authHeader() });

export const markReadyByStaff  = (id) =>
  axios.put(`${ORDER_API_BASE_URL}/${id}/ready`, {}, { headers: authHeader() });

export const fulfillOrderByStaff = (id) =>
  axios.put(`${ORDER_API_BASE_URL}/${id}/fulfill`, {}, { headers: authHeader() });

export const deleteOrder = (id) => {
  return axios.delete(`${ORDER_API_BASE_URL}/${id}`, { headers: authHeader() });
};

export const getOrderById = (id) => axios.get(`${ORDER_API_BASE_URL}/${id}`);


export const updateOrder = (id, orderDto) => {
  return axios.put(`${ORDER_API_BASE_URL}/${id}`, orderDto, {
    headers: {
      "Content-Type": "application/json",
      ...authHeader(),
    },
  });
};

export const listMyOrders = () => {
  return axios.get(`${ORDER_API_BASE_URL}/myorders`, {
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
