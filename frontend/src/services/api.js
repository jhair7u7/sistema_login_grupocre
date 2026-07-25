import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Agrega automáticamente el JWT a todas las peticiones
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// El backend espera { login, password }
export const loginRequest = (login, password) => {
  return api.post("/auth/login", {
    login,
    password,
  });
};

export const registerRequest = (data) =>
  api.post("/auth/register", data);

export const forgotPasswordRequest = (email) =>
  api.post("/auth/forgot-password", { email });

export const resetPasswordRequest = (data) =>
  api.post("/auth/reset-password", data);

export const logoutRequest = () =>
  api.post("/auth/logout");

export default api;