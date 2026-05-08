import { api } from "./api";
import type { EvolucaoAlunoResponse, EvolucaoDashboardResponse } from "./types";

export type EvolucaoCreate = {
  alunoId: number;
  tipo: string;
  descricao: string;
  data: string; // ISO
};

export const evolucoesService = {
  list: (alunoId?: number) => {
    const qs = new URLSearchParams();
    if (alunoId !== undefined) qs.set("alunoId", String(alunoId));
    const q = qs.toString();
    return api.get<EvolucaoAlunoResponse[]>(`/evolucoes${q ? `?${q}` : ""}`);
  },
  create: (body: EvolucaoCreate) => api.post<EvolucaoAlunoResponse>("/evolucoes", body),
  dashboard: (alunoId: number) => api.get<EvolucaoDashboardResponse>(`/dashboard/evolucao/aluno/${alunoId}`)
};
