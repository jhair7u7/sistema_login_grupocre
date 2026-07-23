import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // cámbialo cuando el backend esté en Render
  headers: {
    "Content-Type": "application/json",
  },
});

export const loginRequest = (email, password) => {
  return api.post("/auth/login", { email, password });
};

export default api;