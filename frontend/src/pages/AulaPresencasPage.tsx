import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { type ApiError } from "../services/api";
import { aulasService } from "../services/aulas";
import type { PresencaAulaResponse, PresencaAulaStatus } from "../services/types";
import { useAuth } from "../state/auth";

export default function AulaPresencasPage() {
  const auth = useAuth();
  const isStaff = auth.role === "ADMIN" || auth.role === "PROFESSOR";
  const params = useParams();
  const aulaId = Number(params.aulaId);

  const [items, setItems] = useState<PresencaAulaResponse[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  const [alunoId, setAlunoId] = useState("");
  const [status, setStatus] = useState<PresencaAulaStatus>("PRESENTE");
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    try {
      const d = await aulasService.presencas.list(aulaId);
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
    if (!isStaff) {
      setLoading(false);
      setItems(null);
      return;
    }
    if (!aulaId || Number.isNaN(aulaId)) {
      setLoading(false);
      setErr("Aula invalida.");
      return;
    }
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isStaff, aulaId]);

  async function upsert(kind: "create" | "update") {
    setSaving(true);
    setErr(null);
    try {
      const body = { alunoId: Number(alunoId), status };
      if (kind === "create") await aulasService.presencas.create(aulaId, body);
      else await aulasService.presencas.update(aulaId, body);
      setAlunoId("");
      setStatus("PRESENTE");
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  if (!isStaff) return <Alert kind="info" message="Apenas PROFESSOR/ADMIN registra presenca." />;
  if (loading) return <Spinner />;
  if (err) return <Alert message={err} />;

  const list = items ?? [];
  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Presencas</div>
        <div className="mt-1 text-sm text-muted">Aula #{aulaId}</div>
      </div>

      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
          <Input label="Aluno ID" value={alunoId} onChange={(e) => setAlunoId(e.target.value)} />
          <label className="block">
            <span className="block text-sm text-muted">Status</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
              value={status}
              onChange={(e) => setStatus(e.target.value as PresencaAulaStatus)}
            >
              <option value="PRESENTE">PRESENTE</option>
              <option value="AUSENTE">AUSENTE</option>
              <option value="JUSTIFICADO">JUSTIFICADO</option>
            </select>
          </label>
          <div className="flex items-end gap-2">
            <Button
              onClick={() => upsert("create")}
              disabled={saving || !alunoId.trim() || Number.isNaN(Number(alunoId))}
            >
              {saving ? <Spinner /> : "Registrar"}
            </Button>
            <Button
              variant="ghost"
              onClick={() => upsert("update")}
              disabled={saving || !alunoId.trim() || Number.isNaN(Number(alunoId))}
            >
              Atualizar
            </Button>
          </div>
        </div>
      </Card>

      <Card>
        {list.length === 0 ? (
          <div className="text-sm text-muted">Nenhuma presenca registrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Aluno</th>
                  <th className="py-2 text-left font-medium">Status</th>
                  <th className="py-2 text-left font-medium">Registrado em</th>
                </tr>
              </thead>
              <tbody>
                {list.map((p) => (
                  <tr key={p.id} className="border-b border-border/60">
                    <td className="py-2">{p.alunoId}</td>
                    <td className="py-2">{p.status}</td>
                    <td className="py-2 text-muted">{new Date(p.registradaEm).toLocaleString()}</td>
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
