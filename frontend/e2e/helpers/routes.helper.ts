export const UI_BASE = process.env.E2E_UI_BASE ?? "http://127.0.0.1:5173";
export const API_BASE = process.env.E2E_API_BASE ?? "http://localhost:8080";

export const routes = {
  login: "/login",
  dashboard: "/dashboard",
  adminDashboard: "/dashboard/admin",
  academias: "/academias",
  alunos: "/alunos",
  atletas: "/atletas",
  mensalidades: "/mensalidades",
  agenda: "/agenda",
  graduacoes: "/graduacoes",
  perfil: "/perfil",
  lutas: "/lutas",
  competicoes: "/competicoes",
  treinos: "/treinos"
} as const;

