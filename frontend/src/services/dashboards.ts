import { api } from "./api";
import type { AdminDashboardResponse, AlunosDashboardResponse, FinanceiroDashboardResponse } from "./types";

export const dashboardsService = {
  admin: () => api.get<AdminDashboardResponse>("/dashboard/admin"),
  alunos: () => api.get<AlunosDashboardResponse>("/dashboard/alunos"),
  financeiro: () => api.get<FinanceiroDashboardResponse>("/dashboard/financeiro")
};

