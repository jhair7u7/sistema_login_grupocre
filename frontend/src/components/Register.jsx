import { useState } from "react";
import { User, Mail, Lock, Eye, EyeOff } from "lucide-react";
import { registerRequest } from "../services/api";
import Alert from "./Alert";

export default function Register({ volverLogin }) {
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    email: "",
    username: "",
    password: "",
    confirmarPassword: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [alert, setAlert] = useState({
    show: false,
    type: "",
    title: "",
    message: "",
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.password !== form.confirmarPassword) {
      setAlert({
        show: true,
        type: "error",
        title: "Error",
        message: "Las contraseñas no coinciden.",
      });
      return;
    }

    setLoading(true);

    try {
      await registerRequest({
        nombre: form.nombre,
        apellido: form.apellido,
        email: form.email,
        username: form.username,
        password: form.password,
      });

      setAlert({
        show: true,
        type: "success",
        title: "Registro exitoso",
        message: "Usuario registrado correctamente.",
      });

      setTimeout(() => {
        volverLogin();
      }, 2000);
    } catch (error) {
      const mensaje =
        error.response?.data?.error ||
        error.response?.data?.message ||
        "No se pudo registrar el usuario.";

      setAlert({
        show: true,
        type: "error",
        title: "Error",
        message: mensaje,
      });
    }

    setLoading(false);
  };

  return (
    <div className="login-container">
      {alert.show && (
        <Alert
          type={alert.type}
          title={alert.title}
          message={alert.message}
          onClose={() => setAlert({ ...alert, show: false })}
        />
      )}

      <form className="login-card" onSubmit={handleSubmit}>
        <h2>Crear cuenta</h2>

        <div className="input-group">
          <User size={18} />
          <input
            type="text"
            name="nombre"
            placeholder="Nombre"
            value={form.nombre}
            onChange={handleChange}
            required
          />
        </div>

        <div className="input-group">
          <User size={18} />
          <input
            type="text"
            name="apellido"
            placeholder="Apellido"
            value={form.apellido}
            onChange={handleChange}
            required
          />
        </div>

        <div className="input-group">
          <Mail size={18} />
          <input
            type="email"
            name="email"
            placeholder="Correo electrónico"
            value={form.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="input-group">
          <User size={18} />
          <input
            type="text"
            name="username"
            placeholder="Nombre de usuario"
            value={form.username}
            onChange={handleChange}
            required
          />
        </div>

        <div className="input-group">
          <Lock size={18} />
          <input
            type={showPassword ? "text" : "password"}
            name="password"
            placeholder="Contraseña"
            value={form.password}
            onChange={handleChange}
            required
          />

          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        </div>

        <div className="input-group">
          <Lock size={18} />
          <input
            type={showPassword ? "text" : "password"}
            name="confirmarPassword"
            placeholder="Confirmar contraseña"
            value={form.confirmarPassword}
            onChange={handleChange}
            required
          />
        </div>

        <button disabled={loading}>
          {loading ? "Registrando..." : "Registrarse"}
        </button>

        <p style={{ marginTop: "15px", textAlign: "center" }}>
          ¿Ya tienes cuenta?{" "}
          <span
            style={{
              color: "#2563eb",
              cursor: "pointer",
              fontWeight: "bold",
            }}
            onClick={volverLogin}
          >
            Iniciar sesión
          </span>
        </p>
      </form>
    </div>
  );
}