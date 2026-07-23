import { AlertTriangle } from "lucide-react";
import "./Alert.css";

export default function Alert({ type, title, message }) {
  return (
    <div className={`login-alert ${type}`}>
      <AlertTriangle size={20} />
      <div className="alert-content">
        <span className="alert-title">{title}</span>
        <p>{message}</p>
      </div>
    </div>
  );
}