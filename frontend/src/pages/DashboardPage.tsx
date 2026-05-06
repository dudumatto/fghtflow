import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Card from "../components/Card";
import Skeleton from "../components/Skeleton";
import { api, type ApiError } from "../services/api";
import type { AtletaDashboardResponse } from "../services/types";

function pct(x: number) {
  return `${Math.round(x * 100)}%`;
}

export default function DashboardPage() {
  const [data, setData] = useState<AtletaDashboardResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    api
      .get<AtletaDashboardResponse>("/dashboard/atleta")
      .then((d) => {
        if (!alive) return;
        setData(d);
        setErr(null);
      })
      .catch((e: ApiError) => {
        if (!alive) return;
        setErr(`${e.status}: ${e.message}`);
      })
      .finally(() => {
        if (!alive) return;
        setLoading(false);
      });
    return () => {
      alive = false;
    };
  }, []);

  if (loading) {
    return (
      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-4">
        {Array.from({ length: 4 }).map((_, i) => (
          <Card key={i}>
            <Skeleton className="h-4 w-24" />
            <Skeleton className="mt-3 h-7 w-16" />
          </Card>
        ))}
      </div>
    );
  }
  if (err) return <Alert message={err} />;
  if (!data) return <Alert kind="info" message="Sem dados ainda" />;

  return (
    <div>
      <div className="mb-4">
        <div className="text-xl font-semibold">Dashboard</div>
        <div className="mt-1 text-sm text-muted">Resumo de performance</div>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <div className="text-sm text-muted">Total lutas</div>
          <div className="mt-1 text-2xl font-semibold">{data.totalFights}</div>
        </Card>
        <Card>
          <div className="text-sm text-muted">Vitorias</div>
          <div className="mt-1 text-2xl font-semibold">{data.wins}</div>
        </Card>
        <Card>
          <div className="text-sm text-muted">Derrotas</div>
          <div className="mt-1 text-2xl font-semibold">{data.losses}</div>
        </Card>
        <Card>
          <div className="text-sm text-muted">Winrate</div>
          <div className="mt-1 text-2xl font-semibold">{pct(data.winrate)}</div>
        </Card>
      </div>

      <div className="mt-3 grid grid-cols-1 gap-3 md:grid-cols-2">
        <Card>
          <div className="text-sm text-muted">Submission rate</div>
          <div className="mt-1 text-2xl font-semibold">{pct(data.submissionRate)}</div>
          <div className="mt-1 text-sm text-muted">Porcentagem de lutas vencidas por finalizacao (sobre o total)</div>
        </Card>
      </div>
    </div>
  );
}
