import { useState } from "react";
import "./Login.css";

export default function ForgotPassword({ volver }) {

  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");

  const enviar = (e) => {
    e.preventDefault();

    if (!email.trim()) {
      setMensaje("Ingrese un correo.");
      return;
    }

    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regex.test(email)) {
      setMensaje("Correo inválido.");
      return;
    }

    // Simulación hasta que exista backend
    setMensaje(
      "Si el correo existe en el sistema, se enviará un enlace de recuperación."
    );
  };

  return (
    <div className="login-wrapper">
      <div className="login-card">

        <h2>Recuperar contraseña</h2>

        <form onSubmit={enviar} className="login-form">

          <div className="input-group">

            <label>Correo electrónico</label>

            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="correo@grupocre.pe"
            />

          </div>

          <button className="login-btn">
            Enviar
          </button>

        </form>

        {mensaje &&

          <p style={{marginTop:"20px"}}>

            {mensaje}

          </p>

        }

        <button
          className="login-btn"
          style={{marginTop:"15px"}}
          onClick={volver}
        >
          Volver al Login
        </button>

      </div>
    </div>
  );

}