import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { type ApiError } from "../services/api";
import { planosService } from "../services/planos";
import type { ApiPage, PlanoResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function PlanosPage() {
  const auth = useAuth();
  const allowed = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [pageData, setPageData] = useState<ApiPage<PlanoResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [ativo, setAtivo] = useState<string>("true");

  const [nome, setNome] = useState("");
  const [descricao, setDescricao] = useState("");
  const [valor, setValor] = useState("120");
  const [duracaoEmDias, setDuracaoEmDias] = useState("30");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!allowed) {
      setLoading(false);
      setPageData(null);
      return;
    }
    let alive = true;
    const load = async () => {
      setLoading(true);
      try {
        const d = await planosService.list({ page, ativo: ativo === "" ? undefined : ativo === "true" });
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
  }, [allowed, page, ativo]);

  async function create() {
    setSaving(true);
    setErr(null);
    try {
      await planosService.create({
        nome: nome.trim(),
        descricao: descricao.trim() ? descricao.trim() : null,
        valor: Number(valor),
        duracaoEmDias: Number(duracaoEmDias),
        ativo: true
      });
      setNome("");
      setDescricao("");
      setValor("120");
      setDuracaoEmDias("30");
      setPage(0);
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  async function toggleAtivo(p: PlanoResponse) {
    setErr(null);
    try {
      await planosService.update(p.id, { ativo: !p.ativo });
      const d = await planosService.list({ page, ativo: ativo === "" ? undefined : ativo === "true" });
      setPageData(d);
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    }
  }

  async function remove(p: PlanoResponse) {
    if (!confirm(`Remover plano "${p.nome}"?`)) return;
    setErr(null);
    try {
      await planosService.remove(p.id);
      const d = await planosService.list({ page, ativo: ativo === "" ? undefined : ativo === "true" });
      setPageData(d);
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    }
  }

  if (!allowed) return <Alert kind="info" message="Apenas PROFESSOR/ADMIN." />;
  if (loading) return <Spinner />;
  if (err) return <Alert message={err} />;

  const items = pageData?.items ?? [];
  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Planos</div>
        <div className="mt-1 text-sm text-muted">Valores e disponibilidade</div>
      </div>

      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <Input label="Nome" value={nome} onChange={(e) => setNome(e.target.value)} />
          <Input label="Descricao" value={descricao} onChange={(e) => setDescricao(e.target.value)} />
          <Input label="Valor" value={valor} onChange={(e) => setValor(e.target.value)} />
          <Input label="Duracao (dias)" value={duracaoEmDias} onChange={(e) => setDuracaoEmDias(e.target.value)} />
        </div>
        <div className="mt-3">
          <Button
            onClick={create}
            disabled={
              saving ||
              !nome.trim() ||
              Number.isNaN(Number(valor)) ||
              Number.isNaN(Number(duracaoEmDias)) ||
              Number(duracaoEmDias) < 1
            }
          >
            {saving ? <Spinner /> : "Criar plano"}
          </Button>
        </div>
      </Card>

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <label className="block">
          <span className="block text-sm text-muted">Ativo</span>
          <select
            className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
            value={ativo}
            onChange={(e) => {
              setPage(0);
              setAtivo(e.target.value);
            }}
          >
            <option value="true">Somente ativos</option>
            <option value="false">Somente inativos</option>
            <option value="">Todos</option>
          </select>
        </label>
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
          <div className="text-sm text-muted">Nenhum plano cadastrado.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Nome</th>
                  <th className="py-2 text-left font-medium">Valor</th>
                  <th className="py-2 text-left font-medium">Ativo</th>
                  <th className="py-2 text-left font-medium">Acoes</th>
                </tr>
              </thead>
              <tbody>
                {items.map((p) => (
                  <tr key={p.id} className="border-b border-border/60">
                    <td className="py-2">{p.nome}</td>
                    <td className="py-2">{p.valor}</td>
                    <td className="py-2">{p.ativo ? "Sim" : "Nao"}</td>
                    <td className="py-2">
                      <div className="flex flex-wrap gap-2">
                        <Button size="sm" variant="ghost" onClick={() => toggleAtivo(p)}>
                          {p.ativo ? "Desativar" : "Ativar"}
                        </Button>
                        <Button size="sm" variant="danger" onClick={() => remove(p)}>
                          Remover
                        </Button>
                      </div>
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
