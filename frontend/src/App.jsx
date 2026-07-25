import { useState } from "react";
import Login from "./pages/Login";
import Register from "./components/Register";
import ForgotPassword from "./pages/ForgotPassword";
import AdminDashboard from "./pages/AdminDashboard";
import { AuthProvider } from "./context/AuthContext";
import "./App.css";

function App() {

  const [vista, setVista] = useState("login");

  return (
    <AuthProvider>

      {vista === "login" && (
        <Login
          abrirRecuperar={() => setVista("recuperar")}
          abrirAdministrador={() => setVista("admin")}
          abrirRegistro={() => setVista("registrar")}
        />
      )}

      {vista === "registrar" && (
        <Register volverLogin={() => setVista("login")} />
      )}

      {vista === "recuperar" && (
        <ForgotPassword
          volver={() => setVista("login")}
        />
      )}

      {vista === "admin" && (
        <AdminDashboard
          volver={() => setVista("login")}
        />
      )}

    </AuthProvider>
  );
}

export default App;