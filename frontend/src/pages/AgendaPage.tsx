import { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { type ApiError } from "../services/api";
import { aulasService } from "../services/aulas";
import type { ApiPage, AulaResponse, AulaTipo } from "../services/types";
import { useAuth } from "../state/auth";

function parseIsoFromLocal(dt: string) {
  if (!dt) return "";
  return new Date(dt).toISOString();
}

export default function AgendaPage() {
  const auth = useAuth();
  const isStaff = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [pageData, setPageData] = useState<ApiPage<AulaResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [page, setPage] = useState(0);

  const [q, setQ] = useState("");
  const [tipo, setTipo] = useState<string>("");
  const [ativa, setAtiva] = useState<string>("true");
  const [from, setFrom] = useState<string>("");
  const [to, setTo] = useState<string>("");

  const [createTipo, setCreateTipo] = useState<AulaTipo>("COLETIVA");
  const [createTitulo, setCreateTitulo] = useState("");
  const [createDescricao, setCreateDescricao] = useState("");
  const [createInicio, setCreateInicio] = useState<string>(() => new Date().toISOString().slice(0, 16));
  const [createDur, setCreateDur] = useState("60");
  const [createCap, setCreateCap] = useState("20");
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    try {
      const d = await aulasService.list({
        page,
        q: q.trim() || undefined,
        tipo: (tipo || undefined) as AulaTipo | undefined,
        ativa: ativa === "" ? undefined : ativa === "true",
        dateFrom: from ? parseIsoFromLocal(from) : undefined,
        dateTo: to ? parseIsoFromLocal(to) : undefined
      });
      setPageData(d);
      setErr(null);
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    let alive = true;
    load().finally(() => {
      if (!alive) return;
    });
    return () => {
      alive = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, q, tipo, ativa, from, to]);

  async function create() {
    setSaving(true);
    setErr(null);
    try {
      const inicioIso = parseIsoFromLocal(createInicio);
      const inicio = new Date(inicioIso);
      const fim = new Date(inicio.getTime() + Number(createDur) * 60_000);
      await aulasService.create({
        tipo: createTipo,
        titulo: createTitulo.trim(),
        descricao: createDescricao.trim() ? createDescricao.trim() : null,
        dataHoraInicio: inicioIso,
        dataHoraFim: fim.toISOString(),
        capacidade: createTipo === "PARTICULAR" ? 1 : Number(createCap)
      });
      setCreateTitulo("");
      setCreateDescricao("");
      setCreateInicio(new Date().toISOString().slice(0, 16));
      setCreateDur("60");
      setCreateCap("20");
      setPage(0);
      await load();
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
        <div className="text-xl font-semibold">Agenda</div>
        <div className="mt-1 text-sm text-muted">Aulas</div>
      </div>

      {isStaff ? (
        <Card>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
            <label className="block">
              <span className="block text-sm text-muted">Tipo</span>
              <select
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                value={createTipo}
                onChange={(e) => setCreateTipo(e.target.value as AulaTipo)}
              >
                <option value="COLETIVA">COLETIVA</option>
                <option value="PARTICULAR">PARTICULAR</option>
              </select>
            </label>
            <Input label="Titulo" value={createTitulo} onChange={(e) => setCreateTitulo(e.target.value)} />
            <Input label="Descricao" value={createDescricao} onChange={(e) => setCreateDescricao(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Inicio</span>
              <input
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                type="datetime-local"
                value={createInicio}
                onChange={(e) => setCreateInicio(e.target.value)}
              />
            </label>
            <Input label="Duracao (min)" value={createDur} onChange={(e) => setCreateDur(e.target.value)} />
            <Input
              label={`Capacidade${createTipo === "PARTICULAR" ? " (fixa 1)" : ""}`}
              value={createTipo === "PARTICULAR" ? "1" : createCap}
              onChange={(e) => setCreateCap(e.target.value)}
              disabled={createTipo === "PARTICULAR"}
            />
          </div>
          <div className="mt-3">
            <Button
              onClick={create}
              disabled={
                saving || !createTitulo.trim() || Number.isNaN(Number(createDur)) || Number.isNaN(Number(createCap))
              }
            >
              {saving ? <Spinner /> : "Criar aula"}
            </Button>
          </div>
        </Card>
      ) : null}

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div className="grid grid-cols-1 gap-3 md:grid-cols-5">
          <Input
            label="Busca"
            value={q}
            onChange={(e) => {
              setPage(0);
              setQ(e.target.value);
            }}
          />
          <label className="block">
            <span className="block text-sm text-muted">Tipo</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={tipo}
              onChange={(e) => {
                setPage(0);
                setTipo(e.target.value);
              }}
            >
              <option value="">Todos</option>
              <option value="COLETIVA">COLETIVA</option>
              <option value="PARTICULAR">PARTICULAR</option>
            </select>
          </label>
          <label className="block">
            <span className="block text-sm text-muted">Ativa</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={ativa}
              onChange={(e) => {
                setPage(0);
                setAtiva(e.target.value);
              }}
            >
              <option value="true">Ativas</option>
              <option value="false">Inativas</option>
              <option value="">Todas</option>
            </select>
          </label>
          <label className="block">
            <span className="block text-sm text-muted">De</span>
            <input
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              type="datetime-local"
              value={from}
              onChange={(e) => {
                setPage(0);
                setFrom(e.target.value);
              }}
            />
          </label>
          <label className="block">
            <span className="block text-sm text-muted">Ate</span>
            <input
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              type="datetime-local"
              value={to}
              onChange={(e) => {
                setPage(0);
                setTo(e.target.value);
              }}
            />
          </label>
        </div>
        <div className="flex gap-2">
          <Button variant="ghost" disabled={!pageData?.hasPrev} onClick={() => setPage((p) => Math.max(0, p - 1))}>
            Prev
          </Button>
          <Button variant="ghost" disabled={!pageData?.hasNext} onClick={() => setPage((p) => p + 1)}>
            Next
          </Button>
        </div>
      </Card>

      <Card>
        {items.length === 0 ? (
          <div className="text-sm text-muted">Nenhuma aula encontrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Inicio</th>
                  <th className="py-2 text-left font-medium">Titulo</th>
                  <th className="py-2 text-left font-medium">Tipo</th>
                  <th className="py-2 text-left font-medium">Cap.</th>
                  <th className="py-2 text-left font-medium">Ativa</th>
                  <th className="py-2 text-left font-medium">Acoes</th>
                </tr>
              </thead>
              <tbody>
                {items.map((a) => (
                  <tr key={a.id} className="border-b border-border/60">
                    <td className="py-2">{new Date(a.dataHoraInicio).toLocaleString()}</td>
                    <td className="py-2">{a.titulo}</td>
                    <td className="py-2">{a.tipo}</td>
                    <td className="py-2">{a.capacidade ?? "-"}</td>
                    <td className="py-2">{a.ativa ? "Sim" : "Nao"}</td>
                    <td className="py-2">
                      {isStaff ? (
                        <NavLink className="text-primary hover:underline" to={`/agenda/${a.id}/presencas`}>
                          Presencas
                        </NavLink>
                      ) : (
                        <span className="text-muted">-</span>
                      )}
                    </td>
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
