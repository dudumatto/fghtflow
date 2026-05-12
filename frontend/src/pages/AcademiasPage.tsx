import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { academiasService } from "../services/academias";
import { type ApiError } from "../services/api";
import type { AcademiaResponse } from "../services/types";
import { useAuth } from "../state/auth";

export default function AcademiasPage() {
  const auth = useAuth();
  const allowed = auth.role === "ADMIN" || auth.role === "PROFESSOR";

  const [items, setItems] = useState<AcademiaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const [nome, setNome] = useState("");
  const [endereco, setEndereco] = useState("");
  const [editing, setEditing] = useState<AcademiaResponse | null>(null);
  const [editNome, setEditNome] = useState("");
  const [editEndereco, setEditEndereco] = useState("");

  async function load() {
    setLoading(true);
    try {
      const data = await academiasService.list();
      setItems(data);
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
    let alive = true;
    academiasService
      .select()
      .then(() => load())
      .catch((e: ApiError) => {
        if (!alive) return;
        setErr(`${e.status}: ${e.message}`);
        setLoading(false);
      });
    return () => {
      alive = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [allowed]);

  async function create() {
    setSaving(true);
    setErr(null);
    try {
      await academiasService.create({
        nome: nome.trim(),
        endereco: endereco.trim() ? endereco.trim() : null
      });
      setNome("");
      setEndereco("");
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  function startEdit(item: AcademiaResponse) {
    setEditing(item);
    setEditNome(item.nome);
    setEditEndereco(item.endereco ?? "");
  }

  async function saveEdit() {
    if (!editing) return;
    setSaving(true);
    setErr(null);
    try {
      await academiasService.update(editing.id, {
        nome: editNome.trim(),
        endereco: editEndereco.trim() ? editEndereco.trim() : null,
        ativo: editing.ativo
      });
      setEditing(null);
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  async function inativar(item: AcademiaResponse) {
    setSaving(true);
    setErr(null);
    try {
      await academiasService.remove(item.id);
      await load();
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  if (!allowed) return <Alert kind="info" message="Apenas PROFESSOR/ADMIN." />;
  if (loading) return <Spinner />;

  return (
    <div className="space-y-3">
      <div className="mb-1">
        <div className="text-xl font-semibold">Academias</div>
        <div className="mt-1 text-sm text-muted">Cadastro e vinculo de academias</div>
      </div>

      {err ? <Alert message={err} /> : null}

      <Card>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-[1fr_1fr_auto] md:items-end">
          <Input label="Nome" value={nome} onChange={(e) => setNome(e.target.value)} />
          <Input label="Endereco" value={endereco} onChange={(e) => setEndereco(e.target.value)} />
          <Button onClick={create} disabled={saving || !nome.trim()}>
            {saving ? <Spinner /> : "Criar"}
          </Button>
        </div>
      </Card>

      <Card>
        {items.length === 0 ? (
          <div className="text-sm text-muted">Nenhuma academia encontrada.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-muted">
                <tr className="border-b border-border">
                  <th className="py-2 text-left font-medium">Nome</th>
                  <th className="py-2 text-left font-medium">Endereco</th>
                  <th className="py-2 text-left font-medium">Status</th>
                  <th className="py-2 text-left font-medium">Responsavel</th>
                  <th className="py-2 text-left font-medium">Acoes</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id} className="border-b border-border/60">
                    <td className="py-2">
                      {editing?.id === item.id ? (
                        <Input label="Nome" value={editNome} onChange={(e) => setEditNome(e.target.value)} />
                      ) : (
                        <span>
                          {item.nome} <span className="text-xs text-muted">#{item.id}</span>
                        </span>
                      )}
                    </td>
                    <td className="py-2">
                      {editing?.id === item.id ? (
                        <Input label="Endereco" value={editEndereco} onChange={(e) => setEditEndereco(e.target.value)} />
                      ) : (
                        item.endereco ?? "-"
                      )}
                    </td>
                    <td className="py-2">{item.ativo ? "Ativa" : "Inativa"}</td>
                    <td className="py-2 text-muted">{item.professorResponsavelNome ?? "-"}</td>
                    <td className="py-2">
                      <div className="flex flex-wrap gap-2">
                        {editing?.id === item.id ? (
                          <>
                            <Button size="sm" onClick={saveEdit} disabled={saving || !editNome.trim()}>
                              Salvar
                            </Button>
                            <Button size="sm" variant="ghost" onClick={() => setEditing(null)} disabled={saving}>
                              Cancelar
                            </Button>
                          </>
                        ) : (
                          <>
                            <Button size="sm" variant="ghost" onClick={() => startEdit(item)} disabled={saving}>
                              Editar
                            </Button>
                            {item.ativo ? (
                              <Button size="sm" variant="danger" onClick={() => inativar(item)} disabled={saving}>
                                Inativar
                              </Button>
                            ) : null}
                          </>
                        )}
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
