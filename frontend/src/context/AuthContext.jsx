import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();

function parseJwt(token) {
  try {
    const base64 = token.split(".")[1];
    const payload = JSON.parse(atob(base64));
    return payload;
  } catch (error) {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      const payload = parseJwt(token);

      if (payload) {
        setUser({
          id: payload.sub,
          email: payload.email,
          role: payload.rol,
          token,
        });
      } else {
        localStorage.removeItem("token");
      }
    }

    setLoading(false);
  }, []);

  const login = (token) => {
    localStorage.setItem("token", token);

    const payload = parseJwt(token);

    setUser({
      id: payload.sub,
      email: payload.email,
      role: payload.rol,
      token,
    });
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        loading,
        isAuthenticated: !!user,
        isAdmin: user?.role === "ADMINISTRADOR",
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);