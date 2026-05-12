import { NavLink, Outlet } from "react-router-dom";
import Button from "../components/Button";
import { useAuth } from "../state/auth";

type Role = "ATLETA" | "PROFESSOR" | "ADMIN";
type NavItem = { to: string; label: string; roles?: Role[] };

const nav: NavItem[] = [
  { to: "/dashboard", label: "Dashboard", roles: ["ATLETA"] },
  { to: "/dashboard/admin", label: "Dashboard admin", roles: ["PROFESSOR", "ADMIN"] },
  { to: "/academias", label: "Academias", roles: ["PROFESSOR", "ADMIN"] },
  { to: "/alunos", label: "Alunos", roles: ["PROFESSOR", "ADMIN"] },
  { to: "/atletas", label: "Atletas", roles: ["PROFESSOR", "ADMIN"] },
  { to: "/planos", label: "Planos", roles: ["PROFESSOR", "ADMIN"] },
  { to: "/mensalidades", label: "Mensalidades", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/agenda", label: "Agenda", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/graduacoes", label: "Graduacao", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/evolucoes", label: "Evolucao", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/perfil", label: "Perfil", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/lutas", label: "Lutas", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/competicoes", label: "Competicoes", roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { to: "/treinos", label: "Treinos", roles: ["ATLETA", "PROFESSOR", "ADMIN"] }
];

export default function AppShell() {
  const auth = useAuth();
  const role: Role = (auth.role ?? "ATLETA") as Role;
  const visible = nav.filter((n) => !n.roles || n.roles.includes(role));

  return (
    <div className="min-h-screen">
      <div className="mx-auto grid max-w-6xl grid-cols-1 gap-4 px-4 py-6 md:grid-cols-[220px_1fr]">
        <aside className="rounded-ui border border-border bg-card p-3">
          <div className="px-2 py-1">
            <div className="text-sm font-semibold">FightFlow</div>
            <div className="text-xs text-muted">{auth.role ?? "User"}</div>
          </div>
          <nav className="mt-3 space-y-1">
            {visible.map((n) => (
              <NavLink
                key={n.to}
                to={n.to}
                className={({ isActive }) =>
                  `block rounded-ui px-2 py-2 text-sm transition-colors ${
                    isActive ? "bg-bg text-fg" : "text-muted hover:bg-bg hover:text-fg"
                  }`
                }
              >
                {n.label}
              </NavLink>
            ))}
          </nav>
          <div className="mt-3 px-2">
            <Button variant="ghost" className="w-full" onClick={() => auth.clear()}>
              Sair
            </Button>
          </div>
        </aside>

        <main className="min-w-0">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
