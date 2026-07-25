import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Lock, Eye, EyeOff } from "lucide-react";
import Alert from "../components/Alert";
import { loginRequest, logoutRequest } from "../services/api";
import "./Login.css";

export default function Login({ abrirRecuperar, abrirAdministrador, abrirRegistro, }) {
  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [alert, setAlert] = useState(null);
  const [loginSuccess, setLoginSuccess] = useState(false);

  const { login, logout, isAdmin } = useAuth();

  const handleChange = (e) =>
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });

  const handleSubmit = async (e) => {
    e.preventDefault();

    const email = form.email.trim();
    const password = form.password.trim();

    if (!email || !password) {
      setAlert({
        type: "warning",
        title: "Campos vacíos",
        message: "Completa todos los campos.",
      });
      return;
    }

    try {
      const response = await loginRequest(email, password);

      login(response.data.token);

      setAlert(null);

      // Leer el rol directamente del JWT
      const payload = JSON.parse(atob(response.data.token.split(".")[1]));

      if (payload.rol === "ADMINISTRADOR") {
        abrirAdministrador();
        return;
      }

      setLoginSuccess(true);

    } catch (error) {

      const mensaje =
        error.response?.data?.error ||
        error.response?.data?.message ||
        error.response?.data?.mensaje ||
        "Credenciales inválidas.";

      if (
        mensaje.toLowerCase().includes("bloqueada") ||
        mensaje.toLowerCase().includes("blocked")
      ) {
        setAlert({
          type: "locked",
          title: "Cuenta bloqueada",
          message: mensaje,
        });
      } else {
        setAlert({
          type: "invalid",
          title: "Error",
          message: mensaje,
        });
      }
    }
  };

  const cerrarSesion = async () => {
    try {
      await logoutRequest();
    } catch (e) {}

    logout();
    setLoginSuccess(false);
  };

  return (
    <div className="login-wrapper">
      <div className="login-card">

        <div className="login-header">
          <div className="login-logo">
            <Lock size={28} strokeWidth={2.2} />
          </div>

          <h1>Bienvenido</h1>
          <p>Inicia sesión para continuar</p>
        </div>

        {loginSuccess ? (
          <div className="login-success">

            <p>✅ Inicio de sesión exitoso</p>

            <button
              className="login-btn"
              onClick={cerrarSesion}
            >
              Cerrar sesión
            </button>

          </div>
        ) : (
          <form onSubmit={handleSubmit} className="login-form">

            {alert && (
              <Alert
                type={alert.type}
                title={alert.title}
                message={alert.message}
              />
            )}

            <div className="input-group">
              <label>Correo electrónico</label>

              <input
                type="email"
                name="email"
                placeholder="tucorreo@grupocre.pe"
                value={form.email}
                onChange={handleChange}
              />
            </div>

            <div className="input-group password-group">

              <label>Contraseña</label>

              <div className="password-input-wrapper">

                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  placeholder="••••••••"
                  value={form.password}
                  onChange={handleChange}
                />

                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>

              </div>

            </div>

            <a
              href="#"
              className="forgot-link"
              onClick={(e) => {
                e.preventDefault();
                abrirRecuperar();
              }}
            >
              ¿Olvidaste tu contraseña?
            </a>

            <button
              type="submit"
              className="login-btn"
            >
              Iniciar sesión
            </button>

            <div style={{ textAlign: "center", marginTop: "15px" }}>
              <span>¿No tienes una cuenta? </span>
              <span
                onClick={abrirRegistro}
                style={{
                color: "#2563eb",
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              Registrarse
            </span>
          </div>

          </form>
        )}

      </div>
    </div>
  );
}