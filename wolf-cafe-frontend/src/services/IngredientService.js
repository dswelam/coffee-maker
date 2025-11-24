import axios from 'axios'
import { getToken } from './AuthService'

const BASE_REST_API_URL = 'http://localhost:8080/api/ingredients'

axios.interceptors.request.use(function(config) {
	config.headers['Authorization'] = getToken()
	return config;
}, function(error) {
	// Do something with request error
	return Promise.reject(error);
});


export const getAllIngredientDtos = () => axios.get(BASE_REST_API_URL)
export const createIngredient = (ingredient) => axios.post(BASE_REST_API_URL, ingredient)
export const updateIngredient = (id, ingredient) => axios.put(`${BASE_REST_API_URL}/${id}`, ingredient)
export const deleteIngredient = (id) => axios.delete(`${BASE_REST_API_URL}/${id}`)
export const getIngredientById = (id) => axios.get(BASE_REST_API_URL + "/" + id);

