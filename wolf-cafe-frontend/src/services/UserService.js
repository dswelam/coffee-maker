import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/users";

// Get user by ID
export const getUserById = (id) => {
  return axios.get(`${API_BASE_URL}/${id}`);
};

// Update user
export const updateUser = (id, user) => {
  return axios.put(`${API_BASE_URL}/${id}`, user);
};
