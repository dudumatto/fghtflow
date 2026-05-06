import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { api, type ApiError } from "../services/api";
import type { ApiPage, AtletaProfileResponse, LutaResponse } from "../services/types";

type Metodo = "SUBMISSION" | "POINTS" | "DECISION" | "OTHER";
type Resultado = "WIN" | "LOSS";

export default function LutasPage() {
  const [profile, setProfile] = useState<AtletaProfileResponse | null>(null);
  const [pageData, setPageData] = useState<ApiPage<LutaResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [resultadoFilter, setResultadoFilter] = useState<"" | Resultado>("");
  const [dateFrom, setDateFrom] = useState<string>("");
  const [dateTo, setDateTo] = useState<string>("");

  const [adversarioNome, setAdversarioNome] = useState("");
  const [resultado, setResultado] = useState<Resultado>("WIN");
  const [metodo, setMetodo] = useState<Metodo>("SUBMISSION");
  const [foughtAt, setFoughtAt] = useState<string>(() => new Date().toISOString().slice(0, 16));
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        const p = await api.get<AtletaProfileResponse>("/atletas/me");
        if (!alive) return;
        setProfile(p);
        const qs = new URLSearchParams();
        qs.set("atletaId", String(p.atletaId));
        qs.set("page", "0");
        qs.set("size", "20");
        qs.set("sort", "foughtAt,desc");
        const list = await api.get<ApiPage<LutaResponse>>(`/lutas?${qs.toString()}`);
        if (!alive) return;
        setPageData(list);
        setErr(null);
      } catch (e) {
        const ae = e as ApiError;
        if (!alive) return;
        setErr(`${ae.status}: ${ae.message}`);
      } finally {
        if (!alive) return;
        setLoading(false);
      }
    })();
    return () => {
      alive = false;
    };
  }, []);

  useEffect(() => {
    let alive = true;
    const load = async () => {
      if (!profile) return;
      setLoading(true);
      try {
        const qs = new URLSearchParams();
        qs.set("atletaId", String(profile.atletaId));
        qs.set("page", String(page));
        qs.set("size", "20");
        qs.set("sort", "foughtAt,desc");
        if (resultadoFilter) qs.set("resultado", resultadoFilter);
        if (dateFrom) qs.set("dateFrom", new Date(dateFrom).toISOString());
        if (dateTo) qs.set("dateTo", new Date(dateTo).toISOString());
        const d = await api.get<ApiPage<LutaResponse>>(`/lutas?${qs.toString()}`);
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
  }, [profile, page, resultadoFilter, dateFrom, dateTo]);

  async function create() {
    if (!profile) return;
    setSaving(true);
    setErr(null);
    try {
      const created = await api.post<LutaResponse>("/lutas", {
        atletaId: profile.atletaId,
        competicaoId: null,
        adversarioNome: adversarioNome || null,
        resultado,
        metodo,
        foughtAt: new Date(foughtAt).toISOString()
      });
      setPage(0);
      setAdversarioNome("");
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <Spinner />;
  if (err) return <Alert message={err} />;
  if (!profile) return <Alert kind="info" message="Perfil indisponivel" />;
  const items = pageData?.items ?? [];

  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Lutas</div>
        <div className="mt-1 text-sm text-muted">Historico de lutas</div>
      </div>

      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <Input label="Adversario" value={adversarioNome} onChange={(e) => setAdversarioNome(e.target.value)} />
          <label className="block">
            <span className="block text-sm text-muted">Data/Hora</span>
            <input
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              type="datetime-local"
              value={foughtAt}
              onChange={(e) => setFoughtAt(e.target.value)}
            />
          </label>
          <label className="block">
            <span className="block text-sm text-muted">Resultado</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={resultado}
              onChange={(e) => setResultado(e.target.value as Resultado)}
            >
              <option value="WIN">WIN</option>
              <option value="LOSS">LOSS</option>
            </select>
          </label>
          <label className="block">
            <span className="block text-sm text-muted">Metodo</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={metodo}
              onChange={(e) => setMetodo(e.target.value as Metodo)}
            >
              <option value="SUBMISSION">SUBMISSION</option>
              <option value="POINTS">POINTS</option>
              <option value="DECISION">DECISION</option>
              <option value="OTHER">OTHER</option>
            </select>
          </label>
        </div>
        <div className="mt-3">
          <Button onClick={create} disabled={saving}>
            {saving ? <Spinner /> : "Registrar luta"}
          </Button>
        </div>
      </Card>

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
          <label className="block">
            <span className="block text-sm text-muted">Resultado</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={resultadoFilter}
              onChange={(e) => {
                setPage(0);
                setResultadoFilter(e.target.value as any);
              }}
            >
              <option value="">Todos</option>
              <option value="WIN">WIN</option>
              <option value="LOSS">LOSS</option>
            </select>
          </label>
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
          <div className="text-sm text-muted">Nenhuma luta registrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Data</th>
                  <th className="py-2 text-left font-medium">Adversario</th>
                  <th className="py-2 text-left font-medium">Resultado</th>
                  <th className="py-2 text-left font-medium">Metodo</th>
                </tr>
              </thead>
              <tbody>
                {items.map((it) => (
                  <tr key={it.id} className="border-b border-border/60">
                    <td className="py-2">{new Date(it.foughtAt).toLocaleString()}</td>
                    <td className="py-2">{it.adversarioNome ?? "-"}</td>
                    <td className="py-2">{it.resultado}</td>
                    <td className="py-2">{it.metodo}</td>
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
