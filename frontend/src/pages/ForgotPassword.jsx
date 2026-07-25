import { useState } from "react";
import {forgotPasswordRequest, resetPasswordRequest,} from "../services/api";
import "./Login.css";

export default function ForgotPassword({ volver }) {
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [loading, setLoading] = useState(false);

  const [paso, setPaso] = useState(1);

  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const enviar = async (e) => {
    e.preventDefault();

    if (!email.trim()) {
      setMensaje("Ingrese un correo electrónico.");
      return;
    }

    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regex.test(email)) {
      setMensaje("Correo inválido.");
      return;
    }

    try {
      setLoading(true);

      const res = await forgotPasswordRequest(email);

      setMensaje(
        res.data?.mensaje ||
        res.data?.message ||
        "Si el correo existe, recibirás instrucciones."
      );

      setPaso(2);

    } catch (error) {

      setMensaje(
        error.response?.data?.mensaje ||
        error.response?.data?.message ||
        "No fue posible procesar la solicitud."
      );

    } finally {
      setLoading(false);
    }
  };
  
  const cambiarPassword = async (e) => {
  e.preventDefault();

  if (!token.trim()) {
    setMensaje("Ingrese el token recibido por correo.");
    return;
  }

  if (!newPassword.trim()) {
    setMensaje("Ingrese la nueva contraseña.");
    return;
  }

  if (newPassword !== confirmPassword) {
    setMensaje("Las contraseñas no coinciden.");
    return;
  }

  try {
    setLoading(true);

    const res = await resetPasswordRequest({
      token,
      newPassword,
    });

    setMensaje(
      res.data?.mensaje ||
      res.data?.message ||
      "Contraseña restablecida correctamente."
    );

    setTimeout(() => {
      volver();
    }, 2000);

  } catch (error) {

    setMensaje(
      error.response?.data?.mensaje ||
      error.response?.data?.message ||
      error.response?.data?.error ||
      "No fue posible restablecer la contraseña."
    );

  } finally {
    setLoading(false);
  }
};

  return (
    <div className="login-wrapper">
      <div className="login-card">

        <h2>
          {paso === 1 ? "Recuperar contraseña" : "Restablecer contraseña"}
        </h2>

      {paso === 1 ? (
        <form onSubmit={enviar} className="login-form">

          <div className="input-group">

            <label>Correo electrónico</label>

            <input
              type="email"
              value={email}
              placeholder="correo@grupocre.pe"
              onChange={(e) => setEmail(e.target.value)}
            />

          </div>

          <button
            className="login-btn"
            disabled={loading}
          >
            {loading ? "Enviando..." : "Enviar token"}
          </button>

        </form>
      ) : (

         <form onSubmit={cambiarPassword} className="login-form">

          <div className="input-group">
          <label>Token recibido</label>

          <input
            type="text"
            value={token}
            placeholder="Pegue el token recibido"
            onChange={(e) => setToken(e.target.value)}
          />
          </div>

          <div className="input-group">
            <label>Nueva contraseña</label>

          <input
            type="password"
            value={newPassword}
            placeholder="Nueva contraseña"
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </div>

        <div className="input-group">
          <label>Confirmar contraseña</label>

          <input
          type="password"
          value={confirmPassword}
          placeholder="Confirmar contraseña"
          onChange={(e) => setConfirmPassword(e.target.value)}
          />
        </div>

        <button
          className="login-btn"
          disabled={loading}
        >
          {loading ? "Actualizando..." : "Cambiar contraseña"}
        </button>

      </form>

  )}

  {mensaje && (
    <p style={{ marginTop: "20px" }}>
      {mensaje}
    </p>
  )}

  <button
    className="login-btn"
    style={{ marginTop: "15px" }}
    onClick={volver}
  >
    Volver al Login
  </button>
      </div>
    </div>
  );
}