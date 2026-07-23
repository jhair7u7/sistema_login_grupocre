import { useState } from "react";
import { Lock, Eye, EyeOff, AlertTriangle } from "lucide-react";
import "./Login.css";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [alert, setAlert] = useState(null);
  const [failedAttempts, setFailedAttempts] = useState(0);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
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

    if (failedAttempts >= 3) {
      setAlert({
        type: "locked",
        title: "Cuenta bloqueada",
        message: "Cuenta bloqueada por intentos fallidos. Contacta al administrador.",
      });
      return;
    }

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

    setAlert(null);
    setFailedAttempts(0);
    console.log("Inicio de sesión exitoso", form);
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

        <form onSubmit={handleSubmit} className="login-form">
          {alert && (
            <div className={`login-alert ${alert.type === "locked" ? "locked" : alert.type === "warning" ? "warning" : alert.type === "invalid" ? "invalid" : ""}`}>
              <AlertTriangle size={20} />
              <div className="alert-content">
                <span className="alert-title">{alert.title}</span>
                <p>{alert.message}</p>
              </div>
            </div>
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
      </div>
    </div>
  );
}