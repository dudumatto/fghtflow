import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { academiasService } from "../services/academias";
import type { ApiError } from "../services/api";
import { atletasService } from "../services/atletas";
import type { AcademiaResumoResponse, AtletaResponse } from "../services/types";
import { useAuth } from "../state/auth";

type FormState = {
  nome: string;
  email: string;
  password: string;
  academiaId: string;
  faixa: string;
  grauAtual: string;
  peso: string;
  categoria: string;
};

const emptyForm: FormState = {
  nome: "",
  email: "",
  password: "",
  academiaId: "",
  faixa: "",
  grauAtual: "0",
  peso: "",
  categoria: ""
};

export default function AtletasPage() {
  const auth = useAuth();
  const allowed = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [items, setItems] = useState<AtletaResponse[]>([]);
  const [academias, setAcademias] = useState<AcademiaResumoResponse[]>([]);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [editing, setEditing] = useState<AtletaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  async function load() {
    setLoading(true);
    try {
      const [academiasData, atletasData] = await Promise.all([academiasService.select(), atletasService.list()]);
      setAcademias(academiasData);
      setItems(atletasData);
      setErr(null);
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!allowed) {
      setLoading(false);
      return;
    }
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [allowed]);

  function patchForm(patch: Partial<FormState>) {
    setForm((current) => ({ ...current, ...patch }));
  }

  function startEdit(item: AtletaResponse) {
    setEditing(item);
    setForm({
      nome: item.nome,
      email: item.email,
      password: "",
      academiaId: String(item.academiaId),
      faixa: item.faixa ?? "",
      grauAtual: String(item.grauAtual ?? 0),
      peso: item.peso == null ? "" : String(item.peso),
      categoria: item.categoria ?? ""
    });
  }

  function resetForm() {
    setEditing(null);
    setForm(emptyForm);
  }

  async function submit() {
    setSaving(true);
    setErr(null);
    try {
      const payload = {
        nome: form.nome.trim(),
        email: form.email.trim(),
        password: form.password.trim() || undefined,
        academiaId: Number(form.academiaId),
        faixa: form.faixa.trim() || null,
        grauAtual: Number(form.grauAtual || 0),
        peso: form.peso.trim() ? Number(form.peso) : null,
        categoria: form.categoria.trim() || null
      };
      if (editing) {
        await atletasService.update(editing.id, payload);
      } else {
        await atletasService.create(payload);
      }
      resetForm();
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  async function inativar(item: AtletaResponse) {
    setSaving(true);
    setErr(null);
    try {
      await atletasService.remove(item.id);
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  const canSubmit = form.nome.trim() && form.email.trim() && form.academiaId && (editing || form.password.trim());

  if (!allowed) return <Alert kind="info" message="Apenas PROFESSOR/ADMIN." />;
  if (loading) return <Spinner />;

  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Atletas</div>
        <div className="mt-1 text-sm text-muted">Cadastro esportivo vinculado a academia</div>
      </div>

      {err ? <Alert message={err} /> : null}

      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
          <Input label="Nome" value={form.nome} onChange={(e) => patchForm({ nome: e.target.value })} />
          <Input label="Email" type="email" value={form.email} onChange={(e) => patchForm({ email: e.target.value })} />
          <Input
            label={editing ? "Nova senha" : "Senha"}
            type="password"
            value={form.password}
            onChange={(e) => patchForm({ password: e.target.value })}
          />
          <label className="block">
            <span className="block text-sm text-muted">Academia</span>
            <select
              className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm text-fg focus:border-primary"
              value={form.academiaId}
              onChange={(e) => patchForm({ academiaId: e.target.value })}
            >
              <option value="">Selecione</option>
              {academias.map((academia) => (
                <option key={academia.id} value={academia.id}>
                  {academia.nome}
                </option>
              ))}
            </select>
          </label>
          <Input label="Faixa" value={form.faixa} onChange={(e) => patchForm({ faixa: e.target.value })} />
          <Input label="Grau" type="number" min={0} max={4} value={form.grauAtual} onChange={(e) => patchForm({ grauAtual: e.target.value })} />
          <Input label="Peso" type="number" step="0.1" value={form.peso} onChange={(e) => patchForm({ peso: e.target.value })} />
          <Input label="Categoria" value={form.categoria} onChange={(e) => patchForm({ categoria: e.target.value })} />
        </div>
        <div className="mt-3 flex flex-wrap gap-2">
          <Button onClick={submit} disabled={saving || !canSubmit}>
            {saving ? <Spinner /> : editing ? "Salvar" : "Criar atleta"}
          </Button>
          {editing ? (
            <Button variant="ghost" onClick={resetForm} disabled={saving}>
              Cancelar
            </Button>
          ) : null}
        </div>
      </Card>

      <Card>
        {items.length === 0 ? (
          <div className="text-sm text-muted">Nenhum atleta encontrado.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Atleta</th>
                  <th className="py-2 text-left font-medium">Academia</th>
                  <th className="py-2 text-left font-medium">Faixa</th>
                  <th className="py-2 text-left font-medium">Categoria</th>
                  <th className="py-2 text-left font-medium">Status</th>
                  <th className="py-2 text-left font-medium">Acoes</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id} className="border-b border-border/60">
                    <td className="py-2">
                      {item.nome} <span className="text-xs text-muted">#{item.id}</span>
                      <div className="text-xs text-muted">{item.email}</div>
                    </td>
                    <td className="py-2">{item.academiaNome}</td>
                    <td className="py-2">{item.faixa ?? "-"} / {item.grauAtual} grau</td>
                    <td className="py-2">{item.categoria ?? "-"}</td>
                    <td className="py-2">{item.ativo ? "Ativo" : "Inativo"}</td>
                    <td className="py-2">
                      <div className="flex flex-wrap gap-2">
                        <Button size="sm" variant="ghost" onClick={() => startEdit(item)} disabled={saving}>
                          Editar
                        </Button>
                        {item.ativo ? (
                          <Button size="sm" variant="danger" onClick={() => inativar(item)} disabled={saving}>
                            Inativar
                          </Button>
                        ) : null}
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
