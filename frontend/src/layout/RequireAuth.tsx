import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../state/auth";

export default function RequireAuth({ children }: { children: JSX.Element }) {
  const auth = useAuth();
  const loc = useLocation();
  if (!auth.token) return <Navigate to="/login" replace state={{ from: loc.pathname }} />;
  return children;
}

