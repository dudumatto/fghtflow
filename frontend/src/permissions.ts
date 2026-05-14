import type { Role } from "./services/types";

export type MenuItem = {
  to: string;
  label: string;
};

const staffRoles: Role[] = ["PROFESSOR", "ADMIN"];

const menuByRole: Record<Role, MenuItem[]> = {
  ALUNO: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/mensalidades", label: "Mensalidades" },
    { to: "/agenda", label: "Agenda" },
    { to: "/graduacoes", label: "Graduacao" },
    { to: "/perfil", label: "Perfil" }
  ],
  ATLETA: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/mensalidades", label: "Mensalidades" },
    { to: "/agenda", label: "Agenda" },
    { to: "/graduacoes", label: "Graduacao" },
    { to: "/evolucoes", label: "Evolucao" },
    { to: "/perfil", label: "Perfil" },
    { to: "/lutas", label: "Lutas" },
    { to: "/competicoes", label: "Competicoes" },
    { to: "/treinos", label: "Treinos" }
  ],
  PROFESSOR: [
    { to: "/dashboard/admin", label: "Dashboard admin" },
    { to: "/academias", label: "Academias" },
    { to: "/alunos", label: "Alunos" },
    { to: "/atletas", label: "Atletas" },
    { to: "/planos", label: "Planos" },
    { to: "/mensalidades", label: "Mensalidades" },
    { to: "/agenda", label: "Agenda" },
    { to: "/graduacoes", label: "Graduacao" },
    { to: "/evolucoes", label: "Evolucao" },
    { to: "/competicoes", label: "Competicoes" },
    { to: "/treinos", label: "Treinos" }
  ],
  ADMIN: [
    { to: "/dashboard/admin", label: "Dashboard admin" },
    { to: "/academias", label: "Academias" },
    { to: "/alunos", label: "Alunos" },
    { to: "/atletas", label: "Atletas" },
    { to: "/planos", label: "Planos" },
    { to: "/mensalidades", label: "Mensalidades" },
    { to: "/agenda", label: "Agenda" },
    { to: "/graduacoes", label: "Graduacao" },
    { to: "/evolucoes", label: "Evolucao" },
    { to: "/competicoes", label: "Competicoes" },
    { to: "/treinos", label: "Treinos" }
  ]
};

const routeRoles: Array<{ pattern: RegExp; roles: Role[] }> = [
  { pattern: /^\/$/, roles: ["ALUNO", "ATLETA", "PROFESSOR", "ADMIN"] },
  { pattern: /^\/dashboard$/, roles: ["ALUNO", "ATLETA"] },
  { pattern: /^\/dashboard\/admin$/, roles: staffRoles },
  { pattern: /^\/academias$/, roles: staffRoles },
  { pattern: /^\/alunos$/, roles: staffRoles },
  { pattern: /^\/atletas$/, roles: staffRoles },
  { pattern: /^\/planos$/, roles: staffRoles },
  { pattern: /^\/mensalidades$/, roles: ["ALUNO", "ATLETA", "PROFESSOR", "ADMIN"] },
  { pattern: /^\/agenda$/, roles: ["ALUNO", "ATLETA", "PROFESSOR", "ADMIN"] },
  { pattern: /^\/agenda\/[^/]+\/presencas$/, roles: staffRoles },
  { pattern: /^\/graduacoes$/, roles: ["ALUNO", "ATLETA", "PROFESSOR", "ADMIN"] },
  { pattern: /^\/evolucoes$/, roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { pattern: /^\/perfil$/, roles: ["ALUNO", "ATLETA"] },
  { pattern: /^\/lutas$/, roles: ["ATLETA"] },
  { pattern: /^\/competicoes$/, roles: ["ATLETA", "PROFESSOR", "ADMIN"] },
  { pattern: /^\/treinos$/, roles: ["ATLETA", "PROFESSOR", "ADMIN"] }
];

export function defaultRouteForRole(role: Role | null): string {
  if (role === "PROFESSOR" || role === "ADMIN") return "/dashboard/admin";
  return "/dashboard";
}

export function canAccessRoute(role: Role | null, path: string): boolean {
  if (!role) return false;
  const route = routeRoles.find((r) => r.pattern.test(path));
  return route ? route.roles.includes(role) : false;
}

export function getMenuItemsByRole(role: Role | null): MenuItem[] {
  return role ? menuByRole[role] ?? [] : [];
}
