import { useState } from "react";
import { useAuth } from "../context/AuthContext";

export default function AdminDashboard({ volver }) {

  const { logout } = useAuth();

  const [usuarios, setUsuarios] = useState([
    {
      id: 1,
      email: "usuario@grupocre.pe",
      estado: "Bloqueado",
    },
    {
      id: 2,
      email: "carlos@grupocre.pe",
      estado: "Bloqueado",
    },
    {
      id: 3,
      email: "admin@grupocre.pe",
      estado: "Activo",
    },
  ]);

  const reactivarUsuario = (id) => {
    setUsuarios(
      usuarios.map((usuario) =>
        usuario.id === id
          ? { ...usuario, estado: "Activo" }
          : usuario
      )
    );
  };

  return (
    <div className="login-wrapper">
      <div className="login-card">

        <div className="login-header">
          <h1>🔐 Panel de Administración</h1>
          <p>Administración de usuarios</p>
        </div>

        {usuarios.map((usuario) => (
          <div
            key={usuario.id}
            style={{
              border: "1px solid #ddd",
              borderRadius: "8px",
              padding: "12px",
              marginBottom: "12px",
            }}
          >
            <strong>{usuario.email}</strong>

            <p>
              Estado:{" "}
              <strong
                style={{
                  color:
                    usuario.estado === "Activo"
                      ? "green"
                      : "red",
                }}
              >
                {usuario.estado}
              </strong>
            </p>

            {usuario.estado === "Bloqueado" && (
              <button
                className="login-btn"
                onClick={() => reactivarUsuario(usuario.id)}
              >
                Reactivar usuario
              </button>
            )}
          </div>
        ))}

        <button
          className="login-btn"
          style={{ marginTop: "15px" }}
          onClick={() => {
            logout();
            volver();
          }}
        >
          Cerrar sesión
        </button>

      </div>
    </div>
  );
}