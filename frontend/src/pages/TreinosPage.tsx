import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { api, type ApiError } from "../services/api";
import type { ApiPage, TreinoResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function TreinosPage() {
  const auth = useAuth();
  const [pageData, setPageData] = useState<ApiPage<TreinoResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [dateFrom, setDateFrom] = useState<string>("");
  const [dateTo, setDateTo] = useState<string>("");

  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [startsAt, setStartsAt] = useState<string>(() => new Date().toISOString().slice(0, 16));
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let alive = true;
    const load = async () => {
      setLoading(true);
      try {
        const qs = new URLSearchParams();
        qs.set("page", String(page));
        qs.set("size", "20");
        qs.set("sort", "startsAt,desc");
        if (dateFrom) qs.set("dateFrom", new Date(dateFrom).toISOString());
        if (dateTo) qs.set("dateTo", new Date(dateTo).toISOString());
        const d = await api.get<ApiPage<TreinoResponse>>(`/treinos?${qs.toString()}`);
        if (!alive) return;
        setPageData(d);
        setErr(null);
      } catch (e) {
        const ae = e as ApiError;
        if (!alive) return;
        setErr(`${ae.status}: ${ae.message}`);
      } finally {
        if (!alive) return;
        setLoading(false);
      }
    };
    load();
    return () => {
      alive = false;
    };
  }, [page, dateFrom, dateTo]);

  const canCreate = auth.role === "PROFESSOR" || auth.role === "ADMIN";

  async function create() {
    setSaving(true);
    setErr(null);
    try {
      const created = await api.post<TreinoResponse>("/treinos", {
        startsAt: new Date(startsAt).toISOString(),
        titulo,
        descricao: descricao || null
      });
      setPage(0);
      setTitulo("");
      setDescricao("");
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <Spinner />;
  if (err) return <Alert message={err} />;

  const items = pageData?.items ?? [];
  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Treinos</div>
        <div className="mt-1 text-sm text-muted">Sessões da academia</div>
      </div>

      {canCreate ? (
        <Card>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
            <Input label="Titulo" value={titulo} onChange={(e) => setTitulo(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Data/Hora</span>
              <input
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                type="datetime-local"
                value={startsAt}
                onChange={(e) => setStartsAt(e.target.value)}
              />
            </label>
            <label className="block md:col-span-2">
              <span className="block text-sm text-muted">Descricao</span>
              <textarea
                className="mt-1 min-h-24 w-full rounded-ui border border-border bg-bg px-3 py-2 text-sm"
                value={descricao}
                onChange={(e) => setDescricao(e.target.value)}
              />
            </label>
          </div>
          <div className="mt-3">
            <Button onClick={create} disabled={saving || !titulo.trim()}>
              {saving ? <Spinner /> : "Criar treino"}
            </Button>
          </div>
        </Card>
      ) : null}

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <label className="block">
            <span className="block text-sm text-muted">De</span>
            <input
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              type="datetime-local"
              value={dateFrom}
              onChange={(e) => {
                setPage(0);
                setDateFrom(e.target.value);
              }}
            />
          </label>
          <label className="block">
            <span className="block text-sm text-muted">Ate</span>
            <input
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              type="datetime-local"
              value={dateTo}
              onChange={(e) => {
                setPage(0);
                setDateTo(e.target.value);
              }}
            />
          </label>
        </div>
        <div className="flex gap-2">
          <Button
            variant="ghost"
            disabled={!pageData?.hasPrev}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
          >
            Prev
          </Button>
          <Button variant="ghost" disabled={!pageData?.hasNext} onClick={() => setPage((p) => p + 1)}>
            Next
          </Button>
        </div>
      </Card>

      <Card>
        {items.length === 0 ? (
          <div className="text-sm text-muted">Nenhum treino cadastrado.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Data</th>
                  <th className="py-2 text-left font-medium">Titulo</th>
                  <th className="py-2 text-left font-medium">Descricao</th>
                </tr>
              </thead>
              <tbody>
                {items.map((it) => (
                  <tr key={it.id} className="border-b border-border/60">
                    <td className="py-2">{new Date(it.startsAt).toLocaleString()}</td>
                    <td className="py-2">{it.titulo}</td>
                    <td className="py-2 text-muted">{it.descricao ?? "-"}</td>
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
