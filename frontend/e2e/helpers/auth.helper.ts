import { expect, request, type APIRequestContext, type Page } from "@playwright/test";
import { API_BASE } from "./routes.helper";
import { PASSWORD, unique } from "./test-data.helper";

export { API_BASE, UI_BASE } from "./routes.helper";
export { PASSWORD, unique } from "./test-data.helper";

export type Role = "ALUNO" | "ATLETA" | "PROFESSOR" | "ADMIN";
export type Auth = {
  ctx: APIRequestContext;
  token: string;
  usuarioId: number;
  role: Role;
  academiaId: number | null;
  email: string;
};

export const backendE2ESkipMessage =
  "Defina E2E_RUN_BACKEND=true e mantenha frontend/backend/DB prontos para executar este teste E2E integrado.";

export function shouldRunBackendE2E() {
  return process.env.E2E_RUN_BACKEND === "true";
}

export function hasConfiguredE2EUser() {
  return Boolean(process.env.E2E_EMAIL && process.env.E2E_PASSWORD);
}

export function configuredE2EUser() {
  return {
    email: process.env.E2E_EMAIL ?? "",
    password: process.env.E2E_PASSWORD ?? "",
    role: (process.env.E2E_ROLE as Role | undefined) ?? "ATLETA"
  };
}

export async function newApiContext(token?: string) {
  return request.newContext({
    baseURL: API_BASE,
    extraHTTPHeaders: token ? { Authorization: `Bearer ${token}` } : undefined
  });
}

export async function registerUser(
  role: Role,
  options: { academiaId?: number | null; academiaNome?: string; emailPrefix?: string } = {}
): Promise<Auth> {
  const ctx = await newApiContext();
  const email = `${unique(options.emailPrefix ?? role.toLowerCase())}@fightflow.test`;
  const res = await ctx.post("/auth/register", {
    data: {
      email,
      password: PASSWORD,
      role,
      academiaId: options.academiaId ?? undefined,
      academiaNome: options.academiaNome ?? (options.academiaId ? undefined : `Academia ${unique("e2e")}`)
    }
  });
  expect(res.ok(), await res.text()).toBeTruthy();
  const body = await res.json();
  const data = body.data;
  const authed = await newApiContext(data.token);
  return { ctx: authed, token: data.token, usuarioId: data.usuarioId, role: data.role, academiaId: data.academiaId, email };
}

export async function registerAthlete(academiaId: number | null, emailPrefix = "atleta") {
  return registerUser("ATLETA", { academiaId, emailPrefix });
}

export async function athleteProfile(auth: Auth) {
  const res = await auth.ctx.get("/atletas/me");
  expect(res.ok(), await res.text()).toBeTruthy();
  return (await res.json()).data as { atletaId: number; usuarioId: number; academiaId: number; email: string };
}

export async function createAula(auth: Auth, title = unique("Aula E2E")) {
  const start = new Date(Date.now() + 60 * 60 * 1000);
  const end = new Date(start.getTime() + 60 * 60 * 1000);
  const res = await auth.ctx.post("/aulas", {
    data: {
      tipo: "COLETIVA",
      titulo: title,
      descricao: "Criada por teste E2E",
      dataHoraInicio: start.toISOString(),
      dataHoraFim: end.toISOString(),
      capacidade: 20
    }
  });
  expect(res.ok(), await res.text()).toBeTruthy();
  return (await res.json()).data as { id: number; titulo: string };
}

export async function createPlano(auth: Auth) {
  const res = await auth.ctx.post("/planos", {
    data: { nome: unique("Plano E2E"), descricao: null, valor: 120, duracaoEmDias: 30, ativo: true }
  });
  expect(res.ok(), await res.text()).toBeTruthy();
  return (await res.json()).data as { id: number };
}

export async function seedAuth(page: Page, auth: Auth) {
  await page.addInitScript(({ token, usuarioId, role, academiaId }) => {
    sessionStorage.setItem("ff_token", token);
    localStorage.setItem("ff_usuarioId", String(usuarioId));
    localStorage.setItem("ff_role", role);
    if (academiaId == null) localStorage.removeItem("ff_academiaId");
    else localStorage.setItem("ff_academiaId", String(academiaId));
  }, auth);
}

export async function seedRoleAuth(page: Page, role: Role) {
  await seedAuth(page, {
    ctx: await newApiContext("e2e-local-token"),
    token: "e2e-local-token",
    usuarioId: 1,
    role,
    academiaId: role === "ADMIN" ? null : 1,
    email: `${role.toLowerCase()}@fightflow.e2e`
  });
}
