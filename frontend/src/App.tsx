import { Navigate, Route, Routes } from "react-router-dom";
import AppShell from "./layout/AppShell";
import RequireAuth from "./layout/RequireAuth";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";
import AcademiasPage from "./pages/AcademiasPage";
import PerfilPage from "./pages/PerfilPage";
import LutasPage from "./pages/LutasPage";
import CompeticoesPage from "./pages/CompeticoesPage";
import TreinosPage from "./pages/TreinosPage";
import AlunosPage from "./pages/AlunosPage";
import PlanosPage from "./pages/PlanosPage";
import MensalidadesPage from "./pages/MensalidadesPage";
import AgendaPage from "./pages/AgendaPage";
import AulaPresencasPage from "./pages/AulaPresencasPage";
import GraduacoesPage from "./pages/GraduacoesPage";
import EvolucoesPage from "./pages/EvolucoesPage";
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
          <Route path="/dashboard/admin" element={<AdminDashboardPage />} />
          <Route path="/academias" element={<AcademiasPage />} />
          <Route path="/perfil" element={<PerfilPage />} />
          <Route path="/lutas" element={<LutasPage />} />
          <Route path="/competicoes" element={<CompeticoesPage />} />
          <Route path="/treinos" element={<TreinosPage />} />
          <Route path="/alunos" element={<AlunosPage />} />
          <Route path="/planos" element={<PlanosPage />} />
          <Route path="/mensalidades" element={<MensalidadesPage />} />
          <Route path="/agenda" element={<AgendaPage />} />
          <Route path="/agenda/:aulaId/presencas" element={<AulaPresencasPage />} />
          <Route path="/graduacoes" element={<GraduacoesPage />} />
          <Route path="/evolucoes" element={<EvolucoesPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  );
}
