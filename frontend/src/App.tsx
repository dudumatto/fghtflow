import { Navigate, Route, Routes } from "react-router-dom";
import AppShell from "./layout/AppShell";
import RequireAuth from "./layout/RequireAuth";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import PerfilPage from "./pages/PerfilPage";
import LutasPage from "./pages/LutasPage";
import CompeticoesPage from "./pages/CompeticoesPage";
import TreinosPage from "./pages/TreinosPage";
import { AuthProvider } from "./state/auth";

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route
          element={
            <RequireAuth>
              <AppShell />
            </RequireAuth>
          }
        >
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/perfil" element={<PerfilPage />} />
          <Route path="/lutas" element={<LutasPage />} />
          <Route path="/competicoes" element={<CompeticoesPage />} />
          <Route path="/treinos" element={<TreinosPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  );
}

