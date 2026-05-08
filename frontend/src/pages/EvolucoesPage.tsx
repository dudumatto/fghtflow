import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { type ApiError } from "../services/api";
import { evolucoesService } from "../services/evolucoes";
import type { EvolucaoAlunoResponse, EvolucaoDashboardResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function EvolucoesPage() {
  const auth = useAuth();
  const isStaff = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [alunoId, setAlunoId] = useState<string>("");
  const [items, setItems] = useState<EvolucaoAlunoResponse[] | null>(null);
  const [dash, setDash] = useState<EvolucaoDashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  const [createAlunoId, setCreateAlunoId] = useState("");
  const [tipo, setTipo] = useState("TECNICA");
  const [descricao, setDescricao] = useState("");
  const [data, setData] = useState<string>(() => new Date().toISOString().slice(0, 16));
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    try {
      const d = await evolucoesService.list(alunoId ? Number(alunoId) : undefined);
      setItems(d);
      setErr(null);
      if (alunoId) {
        const db = await evolucoesService.dashboard(Number(alunoId));
        setDash(db);
      } else {
        setDash(null);
      }
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [alunoId]);

  async function create() {
    setSaving(true);
    setErr(null);
    try {
      await evolucoesService.create({
        alunoId: Number(createAlunoId),
        tipo: tipo.trim(),
        descricao: descricao.trim(),
        data: new Date(data).toISOString()
      });
      setCreateAlunoId("");
      setDescricao("");
      setData(new Date().toISOString().slice(0, 16));
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

  const list = items ?? [];
  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Evolucao</div>
        <div className="mt-1 text-sm text-muted">Historico e recomendacoes</div>
      </div>

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <Input label="Aluno ID (opcional)" value={alunoId} onChange={(e) => setAlunoId(e.target.value)} />
        <Button variant="ghost" onClick={load}>
          Recarregar
        </Button>
      </Card>

      {dash ? (
        <Card>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
            <div>
              <div className="text-sm text-muted">Faixa atual</div>
              <div className="mt-1 text-lg font-semibold">
                {(dash.faixaAtual ?? "-") + " / " + dash.grauAtual}
              </div>
            </div>
            <div>
              <div className="text-sm text-muted">Total evolucoes</div>
              <div className="mt-1 text-lg font-semibold">{dash.totalEvolucoes}</div>
            </div>
            <div>
              <div className="text-sm text-muted">Ultima graduacao</div>
              <div className="mt-1 text-lg font-semibold">
                {dash.ultimaGraduacaoEm ? new Date(dash.ultimaGraduacaoEm).toLocaleDateString() : "-"}
              </div>
            </div>
          </div>
          <div className="mt-3 text-sm">
            <div className="font-semibold">Recomendacoes</div>
            {dash.recomendacoes.length === 0 ? (
              <div className="mt-1 text-muted">Nenhuma.</div>
            ) : (
              <ul className="mt-1 list-disc pl-5 text-muted">
                {dash.recomendacoes.map((r, idx) => (
                  <li key={idx}>{r}</li>
                ))}
              </ul>
            )}
          </div>
        </Card>
      ) : null}

      {isStaff ? (
        <Card>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
            <Input label="Aluno ID" value={createAlunoId} onChange={(e) => setCreateAlunoId(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Tipo</span>
              <select
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                value={tipo}
                onChange={(e) => setTipo(e.target.value)}
              >
                <option value="TECNICA">TECNICA</option>
                <option value="FISICO">FISICO</option>
                <option value="TATICO">TATICO</option>
                <option value="MENTAL">MENTAL</option>
                <option value="COMPETICAO">COMPETICAO</option>
                <option value="OBSERVACAO">OBSERVACAO</option>
              </select>
            </label>
            <Input label="Descricao" value={descricao} onChange={(e) => setDescricao(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Data</span>
              <input
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                type="datetime-local"
                value={data}
                onChange={(e) => setData(e.target.value)}
              />
            </label>
          </div>
          <div className="mt-3">
            <Button
              onClick={create}
              disabled={
                saving ||
                !createAlunoId.trim() ||
                Number.isNaN(Number(createAlunoId)) ||
                !tipo.trim() ||
                !descricao.trim()
              }
            >
              {saving ? <Spinner /> : "Registrar evolucao"}
            </Button>
          </div>
        </Card>
      ) : null}

      {!isStaff ? <Alert kind="info" message="ATLETA pode consultar; criacao e de PROFESSOR/ADMIN." /> : null}

      <Card>
        {list.length === 0 ? (
          <div className="text-sm text-muted">Nenhuma evolucao registrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Aluno</th>
                  <th className="py-2 text-left font-medium">Tipo</th>
                  <th className="py-2 text-left font-medium">Descricao</th>
                  <th className="py-2 text-left font-medium">Registrada em</th>
                </tr>
              </thead>
              <tbody>
                {list.map((ev) => (
                  <tr key={ev.id} className="border-b border-border/60">
                    <td className="py-2">{ev.alunoId}</td>
                    <td className="py-2">{ev.tipo}</td>
                    <td className="py-2 text-muted">{ev.descricao}</td>
                    <td className="py-2 text-muted">{new Date(ev.data).toLocaleString()}</td>
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
