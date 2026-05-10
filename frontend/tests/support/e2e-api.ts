import { expect, request, type APIRequestContext, type Page } from "@playwright/test";

export const UI_BASE = process.env.E2E_UI_BASE ?? "http://localhost:5173";
export const API_BASE = process.env.E2E_API_BASE ?? "http://localhost:8080";
export const PASSWORD = "password123";

export type Role = "ATLETA" | "PROFESSOR" | "ADMIN";
export type Auth = {
  ctx: APIRequestContext;
  token: string;
  usuarioId: number;
  role: Role;
  academiaId: number | null;
  email: string;
};

export function unique(prefix: string) {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
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

export function collectCriticalBrowserIssues(page: Page) {
  const issues: string[] = [];
  page.on("console", (msg) => {
    const text = msg.text();
    if (msg.type() === "error" && !text.includes("favicon.ico")) issues.push(`console: ${text}`);
  });
  page.on("response", (res) => {
    const url = res.url();
    const status = res.status();
    if (url.startsWith(API_BASE) && status >= 500) issues.push(`network ${status}: ${url}`);
  });
  return issues;
}
