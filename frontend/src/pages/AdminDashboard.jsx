import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { logoutRequest } from "../services/api";
import api from "../services/api";
import "./Login.css";

export default function AdminDashboard({ volver }) {
  const { logout } = useAuth();

  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);

  const cargarUsuarios = async () => {
    try {
      const res = await api.get("/admin/users/blocked");
      setUsuarios(res.data);
    } catch (error) {
      console.error(error);
      alert("No se pudieron cargar los usuarios bloqueados.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const reactivarUsuario = async (id) => {
    try {
      await api.put(`/admin/users/${id}/unlock`);

      alert("Usuario desbloqueado correctamente.");

      cargarUsuarios();
    } catch (error) {
      alert(
        error.response?.data?.message ||
        "No fue posible desbloquear el usuario."
      );
    }
  };

  const cerrarSesion = async () => {
    try {
      await logoutRequest();
    } catch (e) {}

    logout();
    volver();
  };

  return (
    <div className="login-wrapper">
      <div className="login-card">

        <div className="login-header">
          <h1>🔐 Panel de Administración</h1>
          <p>Usuarios bloqueados</p>
        </div>

        {loading ? (
          <p>Cargando...</p>
        ) : usuarios.length === 0 ? (
          <p>No existen usuarios bloqueados.</p>
        ) : (
          usuarios.map((usuario) => (
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
                Estado:
                <strong style={{ color: "red", marginLeft: "5px" }}>
                  BLOQUEADO
                </strong>
              </p>

              <button
                className="login-btn"
                onClick={() => reactivarUsuario(usuario.id)}
              >
                Reactivar usuario
              </button>
            </div>
          ))
        )}

        <button
          className="login-btn"
          style={{ marginTop: "20px" }}
          onClick={cerrarSesion}
        >
          Cerrar sesión
        </button>

      </div>
    </div>
  );
}