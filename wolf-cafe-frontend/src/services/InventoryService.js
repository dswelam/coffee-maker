import axios from 'axios'
import { getToken } from './AuthService'

const BASE_REST_API_URL = 'http://localhost:8080/api/inventory'

axios.interceptors.request.use(function (config) {
  config.headers['Authorization'] = getToken()
  return config;
}, function (error) {
  // Do something with request error
  return Promise.reject(error);
});


export const getInventory = () => axios.get(BASE_REST_API_URL)
export const updateInventory = (inventory) => axios.put(BASE_REST_API_URL, inventory)
