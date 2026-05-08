import { api } from "./api";
import type { ApiPage, PlanoResponse } from "./types";

export type PlanoCreate = {
  nome: string;
  descricao?: string | null;
  valor: number;
  duracaoEmDias: number;
  ativo?: boolean;
};

export type PlanoUpdate = {
  nome?: string;
  descricao?: string | null;
  valor?: number;
  duracaoEmDias?: number;
  ativo?: boolean;
};

export const planosService = {
  list: (params: { ativo?: boolean; page?: number; size?: number } = {}) => {
    const qs = new URLSearchParams();
    qs.set("page", String(params.page ?? 0));
    qs.set("size", String(params.size ?? 20));
    qs.set("sort", "nome,asc");
    if (params.ativo !== undefined) qs.set("ativo", String(params.ativo));
    return api.get<ApiPage<PlanoResponse>>(`/planos?${qs.toString()}`);
  },
  create: (body: PlanoCreate) => api.post<PlanoResponse>("/planos", body),
  update: (id: number, body: PlanoUpdate) => api.put<PlanoResponse>(`/planos/${id}`, body),
  remove: (id: number) => api.del<void>(`/planos/${id}`)
};
