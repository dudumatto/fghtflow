import { Navigate, Route, Routes } from "react-router-dom";
import AppShell from "./layout/AppShell";
import RequireAuth from "./layout/RequireAuth";
import RoleRoute from "./layout/RoleRoute";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";
import AcademiasPage from "./pages/AcademiasPage";
import PerfilPage from "./pages/PerfilPage";
import LutasPage from "./pages/LutasPage";
import CompeticoesPage from "./pages/CompeticoesPage";
import TreinosPage from "./pages/TreinosPage";
import AlunosPage from "./pages/AlunosPage";
import AtletasPage from "./pages/AtletasPage";
import PlanosPage from "./pages/PlanosPage";
import MensalidadesPage from "./pages/MensalidadesPage";
import AgendaPage from "./pages/AgendaPage";
import AulaPresencasPage from "./pages/AulaPresencasPage";
import GraduacoesPage from "./pages/GraduacoesPage";
import EvolucoesPage from "./pages/EvolucoesPage";
import { defaultRouteForRole } from "./permissions";
import { AuthProvider } from "./state/auth";
import { useAuth } from "./state/auth";

function HomeRedirect() {
  const auth = useAuth();
  return <Navigate to={defaultRouteForRole(auth.role)} replace />;
}

function protectedPage(page: JSX.Element) {
  return <RoleRoute>{page}</RoleRoute>;
}

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
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/dashboard" element={protectedPage(<DashboardPage />)} />
          <Route path="/dashboard/admin" element={protectedPage(<AdminDashboardPage />)} />
          <Route path="/academias" element={protectedPage(<AcademiasPage />)} />
          <Route path="/perfil" element={protectedPage(<PerfilPage />)} />
          <Route path="/lutas" element={protectedPage(<LutasPage />)} />
          <Route path="/competicoes" element={protectedPage(<CompeticoesPage />)} />
          <Route path="/treinos" element={protectedPage(<TreinosPage />)} />
          <Route path="/alunos" element={protectedPage(<AlunosPage />)} />
          <Route path="/atletas" element={protectedPage(<AtletasPage />)} />
          <Route path="/planos" element={protectedPage(<PlanosPage />)} />
          <Route path="/mensalidades" element={protectedPage(<MensalidadesPage />)} />
          <Route path="/agenda" element={protectedPage(<AgendaPage />)} />
          <Route path="/agenda/:aulaId/presencas" element={protectedPage(<AulaPresencasPage />)} />
          <Route path="/graduacoes" element={protectedPage(<GraduacoesPage />)} />
          <Route path="/evolucoes" element={protectedPage(<EvolucoesPage />)} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  );
}
