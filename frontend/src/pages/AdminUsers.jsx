import { useState } from "react";
import "./Login.css";

export default function AdminUsers({ volver }) {

  const [usuarios, setUsuarios] = useState([
    {
      id: 1,
      email: "usuario@grupocre.pe",
      estado: "Bloqueado",
    },
    {
      id: 2,
      email: "admin@grupocre.pe",
      estado: "Activo",
    },
    {
      id: 3,
      email: "carlos@grupocre.pe",
      estado: "Bloqueado",
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

    alert("Usuario reactivado correctamente.");

  };

  return (
    <div className="login-wrapper">
      <div className="login-card">

        <h2>Administración de Usuarios</h2>

        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            marginTop: "20px",
          }}
        >
          <thead>
            <tr>
              <th>Correo</th>
              <th>Estado</th>
              <th>Acción</th>
            </tr>
          </thead>

          <tbody>

            {usuarios.map((usuario) => (

              <tr key={usuario.id}>

                <td>{usuario.email}</td>

                <td>

                  {usuario.estado === "Activo"
                    ? "🟢 Activo"
                    : "🔴 Bloqueado"}

                </td>

                <td>

                  {usuario.estado === "Bloqueado" ? (

                    <button
                      className="login-btn"
                      onClick={() => reactivarUsuario(usuario.id)}
                    >
                      Reactivar
                    </button>

                  ) : (

                    "—"

                  )}

                </td>

              </tr>

            ))}

          </tbody>

        </table>

        <button
          className="login-btn"
          style={{ marginTop: "25px" }}
          onClick={volver}
        >
          Volver
        </button>

      </div>
    </div>
  );
}