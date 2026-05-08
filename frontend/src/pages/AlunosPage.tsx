import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Card from "../components/Card";
import Skeleton from "../components/Skeleton";
import { dashboardsService } from "../services/dashboards";
import type { AlunosDashboardResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function AlunosPage() {
  const auth = useAuth();
  const allowed = auth.role === "ADMIN" || auth.role === "PROFESSOR";
  const [data, setData] = useState<AlunosDashboardResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!allowed) {
      setLoading(false);
      setData(null);
      return;
    }
    let alive = true;
    setLoading(true);
    dashboardsService
      .alunos()
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
        {Array.from({ length: 4 }).map((_, i) => (
          <Card key={i}>
            <Skeleton className="h-4 w-28" />
            <Skeleton className="mt-3 h-7 w-16" />
          </Card>
        ))}
      </div>
    );
  if (err) return <Alert message={err} />;
  if (!data) return <Alert kind="info" message="Sem dados ainda" />;

  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Alunos</div>
        <div className="mt-1 text-sm text-muted">Resumo e inadimplencia</div>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
        <Card>
          <div className="text-sm text-muted">Ativos</div>
          <div className="mt-1 text-2xl font-semibold">{data.alunosAtivos}</div>
        </Card>
        <Card>
          <div className="text-sm text-muted">Novos (30d)</div>
          <div className="mt-1 text-2xl font-semibold">{data.alunosNovos30d}</div>
        </Card>
        <Card>
          <div className="text-sm text-muted">Inadimplentes (bloqueante)</div>
          <div className="mt-1 text-2xl font-semibold">{data.alunosComInadimplenciaBloqueante}</div>
        </Card>
      </div>

      <Card>
        <div className="mb-2 text-sm font-semibold">Top inadimplentes</div>
        {data.topInadimplentes.length === 0 ? (
          <div className="text-sm text-muted">Nenhum.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Aluno</th>
                  <th className="py-2 text-left font-medium">Em aberto</th>
                  <th className="py-2 text-left font-medium">Qtd</th>
                  <th className="py-2 text-left font-medium">Mais antigo</th>
                </tr>
              </thead>
              <tbody>
                {data.topInadimplentes.map((it) => (
                  <tr key={it.alunoId} className="border-b border-border/60">
                    <td className="py-2">
                      {it.nome} <span className="text-xs text-muted">#{it.alunoId}</span>
                    </td>
                    <td className="py-2">{it.totalAberto}</td>
                    <td className="py-2">{it.mensalidadesEmAberto}</td>
                    <td className="py-2 text-muted">{it.oldestVencimento ? new Date(it.oldestVencimento).toLocaleDateString() : "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
}
