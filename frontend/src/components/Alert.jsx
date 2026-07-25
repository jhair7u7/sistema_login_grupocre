import {
  AlertTriangle,
  CheckCircle,
  AlertCircle,
  Lock,
  Info,
} from "lucide-react";
import "./Alert.css";

export default function Alert({ type = "info", title, message }) {
  const icons = {
    success: <CheckCircle size={20} />,
    error: <AlertCircle size={20} />,
    invalid: <AlertCircle size={20} />,
    warning: <AlertTriangle size={20} />,
    locked: <Lock size={20} />,
    info: <Info size={20} />,
  };

  return (
    <div className={`login-alert ${type}`}>
      {icons[type] || icons.info}

      <div className="alert-content">
        <span className="alert-title">{title}</span>
        <p>{message}</p>
      </div>
    </div>
  );
}