import React, { createContext, useContext, useMemo, useState } from "react";
import type { AuthResponse, Role } from "../services/types";

type AuthState = {
  token: string | null;
  usuarioId: number | null;
  role: Role | null;
  academiaId: number | null;
};

type AuthCtx = AuthState & {
  setAuth: (a: AuthResponse) => void;
  clear: () => void;
};

const Ctx = createContext<AuthCtx | null>(null);

function load(): AuthState {
  const token = sessionStorage.getItem("ff_token");
  const usuarioId = localStorage.getItem("ff_usuarioId");
  const role = localStorage.getItem("ff_role") as Role | null;
  const academiaId = localStorage.getItem("ff_academiaId");
  return {
    token,
    usuarioId: usuarioId ? Number(usuarioId) : null,
    role: role ?? null,
    academiaId: academiaId ? Number(academiaId) : null
  };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>(() => load());

  const value = useMemo<AuthCtx>(() => {
    return {
      ...state,
      setAuth: (a) => {
        sessionStorage.setItem("ff_token", a.token);
        localStorage.setItem("ff_usuarioId", String(a.usuarioId));
        localStorage.setItem("ff_role", a.role);
        if (a.academiaId === null) localStorage.removeItem("ff_academiaId");
        else localStorage.setItem("ff_academiaId", String(a.academiaId));
        setState({
          token: a.token,
          usuarioId: a.usuarioId,
          role: a.role,
          academiaId: a.academiaId
        });
      },
      clear: () => {
        // Best-effort server logout (clears refresh cookie).
        const base = (import.meta as any).env?.VITE_API_BASE ?? "http://localhost:8080";
        fetch(`${base}/auth/logout`, { method: "POST", credentials: "include" }).catch(() => {});
        sessionStorage.removeItem("ff_token");
        localStorage.removeItem("ff_usuarioId");
        localStorage.removeItem("ff_role");
        localStorage.removeItem("ff_academiaId");
        setState({ token: null, usuarioId: null, role: null, academiaId: null });
      }
    };
  }, [state]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useAuth() {
  const v = useContext(Ctx);
  if (!v) throw new Error("AuthProvider missing");
  return v;
}
