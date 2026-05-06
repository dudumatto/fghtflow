import { useEffect, useState } from "react";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import Skeleton from "../components/Skeleton";
import { api, type ApiError } from "../services/api";
import type { AtletaProfileResponse } from "../services/types";

export default function PerfilPage() {
  const [profile, setProfile] = useState<AtletaProfileResponse | null>(null);
  const [faixa, setFaixa] = useState("");
  const [peso, setPeso] = useState<string>("");
  const [categoria, setCategoria] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    api
      .get<AtletaProfileResponse>("/atletas/me")
      .then((p) => {
        if (!alive) return;
        setProfile(p);
        setFaixa(p.faixa ?? "");
        setPeso(p.peso == null ? "" : String(p.peso));
        setCategoria(p.categoria ?? "");
        setErr(null);
      })
      .catch((e: ApiError) => {
        if (!alive) return;
        setErr(`${e.status}: ${e.message}`);
      })
      .finally(() => {
        if (!alive) return;
        setLoading(false);
      });
    return () => {
      alive = false;
    };
  }, []);

  async function save() {
    setOk(null);
    setErr(null);
    setSaving(true);
    try {
      const p = await api.put<AtletaProfileResponse>("/atletas/me", {
        faixa: faixa || null,
        peso: peso ? Number(peso) : null,
        categoria: categoria || null
      });
      setProfile(p);
      setOk("Perfil atualizado");
    } catch (e) {
      const ae = e as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <Card className="max-w-xl">
        <Skeleton className="h-4 w-40" />
        <Skeleton className="mt-3 h-10 w-full" />
        <Skeleton className="mt-3 h-10 w-full" />
        <Skeleton className="mt-3 h-10 w-full" />
        <Skeleton className="mt-4 h-10 w-28" />
      </Card>
    );
  }
  if (err) return <Alert message={err} />;
  if (!profile) return <Alert kind="info" message="Perfil indisponivel" />;

  return (
    <div>
      <div className="mb-4">
        <div className="text-xl font-semibold">Perfil</div>
        <div className="mt-1 text-sm text-muted">{profile.email}</div>
      </div>

      <Card className="max-w-xl">
        <div className="space-y-3">
          {ok ? <Alert kind="info" message={ok} /> : null}
          <Input label="Faixa" value={faixa} onChange={(e) => setFaixa(e.target.value)} />
          <Input label="Peso (kg)" inputMode="decimal" value={peso} onChange={(e) => setPeso(e.target.value)} />
          <Input label="Categoria" value={categoria} onChange={(e) => setCategoria(e.target.value)} />
          <div className="pt-1">
            <Button onClick={save} disabled={saving}>
              {saving ? <Spinner /> : "Salvar"}
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}
