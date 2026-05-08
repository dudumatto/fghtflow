import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { type ApiError } from "../services/api";
import { mensalidadesService } from "../services/mensalidades";
import type { ApiPage, MensalidadeResponse, MensalidadeStatus, MetodoPagamento } from "../services/types";
import { useAuth } from "../state/auth";

function parseIsoFromLocal(dt: string) {
  if (!dt) return "";
  return new Date(dt).toISOString();
}

export default function MensalidadesPage() {
  const auth = useAuth();
  const isStaff = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [pageData, setPageData] = useState<ApiPage<MensalidadeResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [page, setPage] = useState(0);

  const [alunoId, setAlunoId] = useState<string>("");
  const [status, setStatus] = useState<string>("");
  const [from, setFrom] = useState<string>("");
  const [to, setTo] = useState<string>("");

  const [createAlunoId, setCreateAlunoId] = useState("");
  const [createPlanoId, setCreatePlanoId] = useState("");
  const [createValor, setCreateValor] = useState("120");
  const [createVenc, setCreateVenc] = useState<string>(() => new Date().toISOString().slice(0, 16));
  const [createMetodo, setCreateMetodo] = useState<MetodoPagamento>("PIX");
  const [createRef, setCreateRef] = useState("");
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    try {
      const d = await mensalidadesService.list({
        page,
        alunoId: isStaff && alunoId ? Number(alunoId) : undefined,
        status: (status || undefined) as MensalidadeStatus | undefined,
        vencimentoFrom: from ? parseIsoFromLocal(from) : undefined,
        vencimentoTo: to ? parseIsoFromLocal(to) : undefined
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
  }, [page, alunoId, status, from, to]);

  async function create() {
    setSaving(true);
    setErr(null);
    try {
      await mensalidadesService.create({
        alunoId: Number(createAlunoId),
        planoId: Number(createPlanoId),
        valor: Number(createValor),
        vencimento: parseIsoFromLocal(createVenc),
        metodoPagamento: createMetodo,
        referencia: createRef.trim() ? createRef.trim() : null
      });
      setCreateAlunoId("");
      setCreatePlanoId("");
      setCreateValor("120");
      setCreateVenc(new Date().toISOString().slice(0, 16));
      setCreateMetodo("PIX");
      setCreateRef("");
      setPage(0);
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  async function pagar(m: MensalidadeResponse) {
    setErr(null);
    try {
      await mensalidadesService.pagar(m.id, {
        metodoPagamento: m.metodoPagamento ?? "MANUAL",
        dataPagamento: new Date().toISOString(),
        referencia: m.referencia ?? null
      });
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    }
  }

  if (loading) return <Spinner />;
  if (err) return <Alert message={err} />;

  const items = pageData?.items ?? [];
  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Mensalidades</div>
        <div className="mt-1 text-sm text-muted">Cobrancas e pagamentos</div>
      </div>

      {isStaff ? (
        <Card>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
            <Input label="Aluno ID" value={createAlunoId} onChange={(e) => setCreateAlunoId(e.target.value)} />
            <Input label="Plano ID" value={createPlanoId} onChange={(e) => setCreatePlanoId(e.target.value)} />
            <Input label="Valor" value={createValor} onChange={(e) => setCreateValor(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Vencimento</span>
              <input
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                type="datetime-local"
                value={createVenc}
                onChange={(e) => setCreateVenc(e.target.value)}
              />
            </label>
            <label className="block">
              <span className="block text-sm text-muted">Metodo pagamento</span>
              <select
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                value={createMetodo}
                onChange={(e) => setCreateMetodo(e.target.value as MetodoPagamento)}
              >
                <option value="PIX">PIX</option>
                <option value="CARTAO">CARTAO</option>
                <option value="BOLETO">BOLETO</option>
                <option value="DINHEIRO">DINHEIRO</option>
                <option value="MANUAL">MANUAL</option>
              </select>
            </label>
            <Input label="Referencia (opcional)" value={createRef} onChange={(e) => setCreateRef(e.target.value)} />
          </div>
          <div className="mt-3">
            <Button
              onClick={create}
              disabled={
                saving ||
                !createAlunoId.trim() ||
                !createPlanoId.trim() ||
                Number.isNaN(Number(createAlunoId)) ||
                Number.isNaN(Number(createPlanoId)) ||
                Number.isNaN(Number(createValor))
              }
            >
              {saving ? <Spinner /> : "Criar mensalidade"}
            </Button>
          </div>
        </Card>
      ) : null}

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
          {isStaff ? <Input label="Aluno ID" value={alunoId} onChange={(e) => { setPage(0); setAlunoId(e.target.value); }} /> : null}
          <label className="block">
            <span className="block text-sm text-muted">Status</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={status}
              onChange={(e) => {
                setPage(0);
                setStatus(e.target.value);
              }}
            >
              <option value="">Todos</option>
              <option value="PENDENTE">PENDENTE</option>
              <option value="ATRASADO">ATRASADO</option>
              <option value="PAGO">PAGO</option>
              <option value="CANCELADO">CANCELADO</option>
            </select>
          </label>
          <label className="block">
            <span className="block text-sm text-muted">Venc. de</span>
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
            <span className="block text-sm text-muted">Venc. ate</span>
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
          <div className="text-sm text-muted">Nenhuma mensalidade encontrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">ID</th>
                  {isStaff ? <th className="py-2 text-left font-medium">Aluno</th> : null}
                  <th className="py-2 text-left font-medium">Vencimento</th>
                  <th className="py-2 text-left font-medium">Valor</th>
                  <th className="py-2 text-left font-medium">Status</th>
                  <th className="py-2 text-left font-medium">Acoes</th>
                </tr>
              </thead>
              <tbody>
                {items.map((m) => (
                  <tr key={m.id} className="border-b border-border/60">
                    <td className="py-2">#{m.id}</td>
                    {isStaff ? <td className="py-2">{m.alunoId}</td> : null}
                    <td className="py-2">{new Date(m.vencimento).toLocaleString()}</td>
                    <td className="py-2">{m.valor}</td>
                    <td className="py-2">{m.status}</td>
                    <td className="py-2">
                      {isStaff && m.status !== "PAGO" && m.status !== "CANCELADO" ? (
                        <Button size="sm" onClick={() => pagar(m)}>
                          Marcar pago
                        </Button>
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
