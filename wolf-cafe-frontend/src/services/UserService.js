import axios from "axios";

const AUTH_REST_API_BASE_URL = "http://localhost:8080/api/auth";

// Get a single user by ID
export const getUserById = (id) => {
  return axios.get(`${AUTH_REST_API_BASE_URL}/users/${id}`);
};

// Update an existing user
export const updateUser = (id, userData) => {
  return axios.put(`${AUTH_REST_API_BASE_URL}/users/${id}`, userData);
};

// Get list of all roles (ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER, etc.)
export const getAllRoles = () => {
  return axios.get(`${AUTH_REST_API_BASE_URL}/roles`);
};

// Fetch all users
export const getAllUsers = () => {
  return axios.get(`${AUTH_REST_API_BASE_URL}/users`);
};

// Delete user by ID
export const deleteUser = (id) => {
  return axios.delete(`${AUTH_REST_API_BASE_URL}/user/${id}`);
};