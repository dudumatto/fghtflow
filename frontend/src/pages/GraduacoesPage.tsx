import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { type ApiError } from "../services/api";
import { graduacoesService } from "../services/graduacoes";
import type { GraduacaoResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function GraduacoesPage() {
  const auth = useAuth();
  const isStaff = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [alunoId, setAlunoId] = useState<string>("");
  const [items, setItems] = useState<GraduacaoResponse[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  const [createAlunoId, setCreateAlunoId] = useState("");
  const [faixa, setFaixa] = useState("BRANCA");
  const [grau, setGrau] = useState("0");
  const [dataGraduacao, setDataGraduacao] = useState<string>(() => new Date().toISOString().slice(0, 16));
  const [obs, setObs] = useState("");
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    try {
      const d = await graduacoesService.list(alunoId ? Number(alunoId) : undefined);
      setItems(d);
      setErr(null);
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
      await graduacoesService.create({
        alunoId: Number(createAlunoId),
        faixa: faixa.trim(),
        grau: Number(grau),
        dataGraduacao: new Date(dataGraduacao).toISOString(),
        observacao: obs.trim() ? obs.trim() : null
      });
      setCreateAlunoId("");
      setDataGraduacao(new Date().toISOString().slice(0, 16));
      setObs("");
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
        <div className="text-xl font-semibold">Graduacao</div>
        <div className="mt-1 text-sm text-muted">Historico por aluno</div>
      </div>

      <Card className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <Input
          label="Aluno ID (opcional)"
          value={alunoId}
          onChange={(e) => setAlunoId(e.target.value)}
        />
        <Button variant="ghost" onClick={load}>
          Recarregar
        </Button>
      </Card>

      {isStaff ? (
        <Card>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
            <Input label="Aluno ID" value={createAlunoId} onChange={(e) => setCreateAlunoId(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Faixa</span>
              <select
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                value={faixa}
                onChange={(e) => setFaixa(e.target.value)}
              >
                <option value="BRANCA">BRANCA</option>
                <option value="AZUL">AZUL</option>
                <option value="ROXA">ROXA</option>
                <option value="MARROM">MARROM</option>
                <option value="PRETA">PRETA</option>
              </select>
            </label>
            <Input label="Grau" value={grau} onChange={(e) => setGrau(e.target.value)} />
            <label className="block">
              <span className="block text-sm text-muted">Data</span>
              <input
                className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                type="datetime-local"
                value={dataGraduacao}
                onChange={(e) => setDataGraduacao(e.target.value)}
              />
            </label>
            <label className="block md:col-span-4">
              <span className="block text-sm text-muted">Observacao</span>
              <textarea
                className="mt-1 min-h-24 w-full rounded-ui border border-border bg-bg px-3 py-2 text-sm"
                value={obs}
                onChange={(e) => setObs(e.target.value)}
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
                !faixa.trim() ||
                Number.isNaN(Number(grau))
              }
            >
              {saving ? <Spinner /> : "Registrar graduacao"}
            </Button>
          </div>
        </Card>
      ) : null}

      <Card>
        {list.length === 0 ? (
          <div className="text-sm text-muted">Nenhuma graduacao registrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Aluno</th>
                  <th className="py-2 text-left font-medium">Faixa</th>
                  <th className="py-2 text-left font-medium">Grau</th>
                  <th className="py-2 text-left font-medium">Data</th>
                  <th className="py-2 text-left font-medium">Obs</th>
                </tr>
              </thead>
              <tbody>
                {list.map((g) => (
                  <tr key={g.id} className="border-b border-border/60">
                    <td className="py-2">{g.alunoId}</td>
                    <td className="py-2">{g.faixa}</td>
                    <td className="py-2">{g.grau}</td>
                    <td className="py-2 text-muted">{new Date(g.dataGraduacao).toLocaleString()}</td>
                    <td className="py-2 text-muted">{g.observacao ?? "-"}</td>
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
