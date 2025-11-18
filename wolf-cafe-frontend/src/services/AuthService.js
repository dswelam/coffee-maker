// src/services/AuthService.js
import axios from 'axios';

const AUTH_REST_API_BASE_URL = 'http://localhost:8080/api/auth';

// Register
export const registerAPICall = (registerObj) =>
  axios.post(AUTH_REST_API_BASE_URL + '/register', registerObj);

// Login
export const loginAPICall = (usernameOrEmail, password) =>
  axios.post(AUTH_REST_API_BASE_URL + '/login', { usernameOrEmail, password });

// Store token if needed
export const storeToken = (token) => localStorage.setItem('token', token);
export const getToken = () => localStorage.getItem('token');

// Save logged-in user information
// Backwards compatible: old code can still call saveLoggedInUser(username, role)
export const saveLoggedInUser = (username, role, id) => {
  sessionStorage.setItem('authenticatedUser', username);
  sessionStorage.setItem('role', role);

  if (id !== undefined && id !== null) {
    sessionStorage.setItem('userId', String(id));
  }
};

// Check if user is logged in
export const isUserLoggedIn = () => {
  return sessionStorage.getItem('authenticatedUser') !== null;
};

// Get username only (legacy)
export const getLoggedInUser = () => {
  return sessionStorage.getItem('authenticatedUser');
};

// Return full user data (id, username, role)
export const getCurrentUser = () => {
  const username = sessionStorage.getItem('authenticatedUser');
  const role = sessionStorage.getItem('role');
  const idStr = sessionStorage.getItem('userId');

  if (!username) return null;

  const id = idStr ? Number(idStr) : null;
  return { id, username, role };
};

// Logout
export const logout = () => {
  localStorage.clear();
  sessionStorage.clear();
};

// Role checks
export const isAdminUser = () => {
  const role = sessionStorage.getItem('role');
  return role === 'ROLE_ADMIN' || role === 'ADMIN';
};

export const isStaffUser = () => {
  const role = sessionStorage.getItem('role');
  return role === 'ROLE_STAFF' || role === 'STAFF';
};

export const isCustomerUser = () => {
  const role = sessionStorage.getItem('role');
  return role === 'ROLE_CUSTOMER' || role === 'CUSTOMER';
};
