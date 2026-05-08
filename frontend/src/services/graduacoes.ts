import { api } from "./api";
import type { GraduacaoResponse } from "./types";

export type GraduacaoCreate = {
  alunoId: number;
  faixa: string;
  grau: number;
  dataGraduacao: string; // ISO
  observacao?: string | null;
};

export const graduacoesService = {
  list: (alunoId?: number) => {
    const qs = new URLSearchParams();
    if (alunoId !== undefined) qs.set("alunoId", String(alunoId));
    const q = qs.toString();
    return api.get<GraduacaoResponse[]>(`/graduacoes${q ? `?${q}` : ""}`);
  },
  create: (body: GraduacaoCreate) => api.post<GraduacaoResponse>("/graduacoes", body)
};
