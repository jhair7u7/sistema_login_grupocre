import { useState } from "react";
import Login from "./pages/Login";
import ForgotPassword from "./pages/ForgotPassword";
import { AuthProvider } from "./context/AuthContext";
import "./App.css";

function App() {

  const [mostrarRecuperar, setMostrarRecuperar] = useState(false);

  return (

    <AuthProvider>

      {
        mostrarRecuperar

        ? <ForgotPassword volver={() => setMostrarRecuperar(false)} />

        : <Login abrirRecuperar={() => setMostrarRecuperar(true)} />

      }

    </AuthProvider>

  );

}

export default App;