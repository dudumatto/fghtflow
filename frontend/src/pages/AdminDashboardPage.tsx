import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Card from "../components/Card";
import Skeleton from "../components/Skeleton";
import { dashboardsService } from "../services/dashboards";
import type { AdminDashboardResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function AdminDashboardPage() {
  const auth = useAuth();
  const [data, setData] = useState<AdminDashboardResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const allowed = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  useEffect(() => {
    if (!allowed) {
      setLoading(false);
      setData(null);
      return;
    }
    let alive = true;
    setLoading(true);
    dashboardsService
      .admin()
      .then((d) => {
        if (!alive) return;
        setData(d);
        setErr(null);
      })
      .catch((e: any) => {
        if (!alive) return;
        setErr(`${e.status ?? 500}: ${e.message ?? "Erro"}`);
      })
      .finally(() => {
        if (!alive) return;
        setLoading(false);
      });
    return () => {
      alive = false;
    };
  }, [allowed]);

  if (!allowed) return <Alert kind="info" message="Apenas PROFESSOR/ADMIN." />;
  if (loading)
    return (
      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-4">
        {Array.from({ length: 8 }).map((_, i) => (
          <Card key={i}>
            <Skeleton className="h-4 w-32" />
            <Skeleton className="mt-3 h-7 w-16" />
          </Card>
        ))}
      </div>
    );
  if (err) return <Alert message={err} />;
  if (!data) return <Alert kind="info" message="Sem dados ainda" />;

  const items: Array<{ label: string; value: number }> = [
    { label: "Alunos ativos", value: data.alunosAtivos },
    { label: "Usuarios atletas", value: data.usuariosAtletas },
    { label: "Usuarios professores", value: data.usuariosProfessores },
    { label: "Usuarios admins", value: data.usuariosAdmins },
    { label: "Planos ativos", value: data.planosAtivos },
    { label: "Matriculas ativas", value: data.matriculasAtivas },
    { label: "Matriculas bloqueadas", value: data.matriculasBloqueadas },
    { label: "Aulas proximas", value: data.aulasProximas }
  ];

  return (
    <div>
      <div className="mb-4">
        <div className="text-xl font-semibold">Dashboard admin</div>
        <div className="mt-1 text-sm text-muted">Visao geral da academia</div>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-4">
        {items.map((it) => (
          <Card key={it.label}>
            <div className="text-sm text-muted">{it.label}</div>
            <div className="mt-1 text-2xl font-semibold">{it.value}</div>
          </Card>
        ))}
      </div>
    </div>
  );
}

