import { Navigate, useLocation } from "react-router-dom";
import { canAccessRoute, defaultRouteForRole } from "../permissions";
import { useAuth } from "../state/auth";

export default function RoleRoute({ children }: { children: JSX.Element }) {
  const auth = useAuth();
  const loc = useLocation();

  if (!canAccessRoute(auth.role, loc.pathname)) {
    return <Navigate to={defaultRouteForRole(auth.role)} replace />;
  }

  return children;
}
