import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

  const [token, setToken] = useState(localStorage.getItem("token"));

  const [usuarios, setUsuarios] = useState([
    {
      id: 1,
      email: "usuario@grupocre.pe",
      password: "123456",
      rol: "USER",
      bloqueado: false,
      intentos: 0,
    },
    {
      id: 2,
      email: "admin@grupocre.pe",
      password: "admin123",
      rol: "ADMIN",
      bloqueado: false,
      intentos: 0,
    },
  ]);

  const login = (newToken) => {
    localStorage.setItem("token", newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  const aumentarIntentos = (email) => {
    setUsuarios((prev) =>
      prev.map((usuario) => {
        if (usuario.email !== email) return usuario;

        const intentos = usuario.intentos + 1;

        return {
          ...usuario,
          intentos,
          bloqueado: intentos >= 3,
        };
      })
    );
  };

  const reiniciarIntentos = (email) => {
    setUsuarios((prev) =>
      prev.map((usuario) =>
        usuario.email === email
          ? {
              ...usuario,
              intentos: 0,
            }
          : usuario
      )
    );
  };

  const reactivarUsuario = (email) => {
    setUsuarios((prev) =>
      prev.map((usuario) =>
        usuario.email === email
          ? {
              ...usuario,
              bloqueado: false,
              intentos: 0,
            }
          : usuario
      )
    );
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        login,
        logout,
        usuarios,
        aumentarIntentos,
        reiniciarIntentos,
        reactivarUsuario,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}