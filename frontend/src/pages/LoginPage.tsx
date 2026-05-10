import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import Spinner from "../components/Spinner";
import { api, type ApiError } from "../services/api";
import type { AuthResponse, Role } from "../services/types";
import { useAuth } from "../state/auth";

type Mode = "login" | "register";

export default function LoginPage() {
  const [mode, setMode] = useState<Mode>("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<Role>("ATLETA");
  const [academiaNome, setAcademiaNome] = useState("");
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const auth = useAuth();
  const nav = useNavigate();

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setErr(null);
    setBusy(true);
    try {
      const payload =
        mode === "login"
          ? { email, password }
          : {
              email,
              password,
              role,
              academiaNome: role === "PROFESSOR" ? academiaNome : academiaNome || undefined
            };
      const path = mode === "login" ? "/auth/login" : "/auth/register";
      const res = await api.post<AuthResponse>(path, payload);
      auth.setAuth(res);
      nav(res.role === "ATLETA" ? "/dashboard" : "/dashboard/admin", { replace: true });
    } catch (e2) {
      const ae = e2 as ApiError;
      setErr(`${ae.status}: ${ae.message}`);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="min-h-screen">
      <div className="mx-auto max-w-md px-4 py-10">
        <div className="mb-6">
          <div className="text-2xl font-semibold">FightFlow</div>
          <div className="mt-1 text-sm text-muted">
            {mode === "login" ? "Acesse sua conta" : "Crie sua conta"}
          </div>
        </div>

        <Card>
          <div className="mb-4 flex gap-2">
            <Button variant={mode === "login" ? "primary" : "ghost"} onClick={() => setMode("login")} type="button">
              Login
            </Button>
            <Button
              variant={mode === "register" ? "primary" : "ghost"}
              onClick={() => setMode("register")}
              type="button"
            >
              Register
            </Button>
          </div>

          {err ? (
            <div className="mb-3">
              <Alert message={err} />
            </div>
          ) : null}

          <form onSubmit={submit} className="space-y-3">
            <Input label="Email" type="email" autoComplete="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <Input
              label="Password"
              type="password"
              autoComplete={mode === "login" ? "current-password" : "new-password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
            />

            {mode === "register" ? (
              <div className="grid grid-cols-1 gap-3">
                <label className="block">
                  <span className="block text-sm text-muted">Role</span>
                  <select
                    className="mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm"
                    value={role}
                    onChange={(e) => setRole(e.target.value as Role)}
                  >
                    <option value="ATLETA">ATLETA</option>
                    <option value="PROFESSOR">PROFESSOR</option>
                  </select>
                </label>
                <Input
                  label="Academia (nome)"
                  placeholder={role === "PROFESSOR" ? "Obrigatorio para professor" : "Opcional"}
                  value={academiaNome}
                  onChange={(e) => setAcademiaNome(e.target.value)}
                  required={role === "PROFESSOR"}
                />
              </div>
            ) : null}

            <div className="pt-1">
              <Button type="submit" className="w-full" disabled={busy}>
                {busy ? <Spinner /> : mode === "login" ? "Entrar" : "Criar conta"}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
}
