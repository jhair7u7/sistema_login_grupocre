import { useState } from "react";
import { Lock, Eye, EyeOff } from "lucide-react";
import Alert from "../components/Alert";
import { loginRequest } from "../services/api";
import { useAuth } from "../context/AuthContext";
import "./Login.css";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [alert, setAlert] = useState(null);
  const [failedAttempts, setFailedAttempts] = useState(0);
  const [loginSuccess, setLoginSuccess] = useState(false);
  const { login } = useAuth();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

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

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setAlert({
        type: "warning",
        title: "Correo inválido",
        message: "Ingresa un correo electrónico con formato válido.",
      });
      return;
    }

    if (failedAttempts >= 3) {
      setAlert({
        type: "locked",
        title: "Cuenta bloqueada",
        message: "Cuenta bloqueada por intentos fallidos. Contacta al administrador.",
      });
      return;
    }

    try {
      const response = await loginRequest(email, password);
      console.log("Login exitoso:", response.data);
      setAlert(null);
      setFailedAttempts(0);
      login(response.data.token);
      setLoginSuccess(true);
    } catch (error) {
      const validEmail = "usuario@grupocree.pe";
      const validPassword = "123456";

      if (email !== validEmail || password !== validPassword) {
        const nextAttempts = failedAttempts + 1;
        setFailedAttempts(nextAttempts);

        if (nextAttempts >= 3) {
          setAlert({
            type: "locked",
            title: "Cuenta bloqueada",
            message: "Cuenta bloqueada por intentos fallidos. Contacta al administrador.",
          });
        } else {
          setAlert({
            type: "invalid",
            title: "Credenciales inválidas",
            message: "Correo o contraseña incorrectos.",
          });
        }
        return;
      }

      console.log("Login simulado exitoso (sin backend aún)", form);
      login("token-simulado-123");
      setLoginSuccess(true);
    }
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
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="login-form">
            {alert && (
              <Alert type={alert.type} title={alert.title} message={alert.message} />
            )}

            <div className="input-group">
              <label>Correo electrónico</label>
              <input
                type="email"
                name="email"
                placeholder="tucorreo@grupocree.pe"
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
                  onClick={() => setShowPassword((state) => !state)}
                  aria-label={showPassword ? "Ocultar contraseña" : "Mostrar contraseña"}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <a href="/recuperar" className="forgot-link">
              ¿Olvidaste tu contraseña?
            </a>

            <button type="submit" className="login-btn">
              Iniciar sesión
            </button>
          </form>
        )}
      </div>
    </div>
  );
  
}