import { api } from "./api";
import type { ApiPage, MensalidadeResponse, MensalidadeStatus, MetodoPagamento } from "./types";

export type MensalidadeCreate = {
  alunoId: number;
  planoId: number;
  valor: number;
  vencimento: string; // ISO
  dataPagamento?: string | null;
  status?: MensalidadeStatus;
  metodoPagamento?: MetodoPagamento;
  referencia?: string | null;
};

export type MensalidadeUpdate = {
  valor?: number;
  vencimento?: string; // ISO
  status?: MensalidadeStatus;
};

export type MensalidadePagamento = {
  metodoPagamento: MetodoPagamento;
  dataPagamento?: string | null; // ISO
  referencia?: string | null;
};

export const mensalidadesService = {
  list: (params: {
    alunoId?: number;
    status?: MensalidadeStatus;
    vencimentoFrom?: string;
    vencimentoTo?: string;
    page?: number;
    size?: number;
  } = {}) => {
    const qs = new URLSearchParams();
    qs.set("page", String(params.page ?? 0));
    qs.set("size", String(params.size ?? 20));
    qs.set("sort", "vencimento,desc");
    if (params.alunoId !== undefined) qs.set("alunoId", String(params.alunoId));
    if (params.status) qs.set("status", params.status);
    if (params.vencimentoFrom) qs.set("vencimentoFrom", params.vencimentoFrom);
    if (params.vencimentoTo) qs.set("vencimentoTo", params.vencimentoTo);
    return api.get<ApiPage<MensalidadeResponse>>(`/mensalidades?${qs.toString()}`);
  },
  create: (body: MensalidadeCreate) => api.post<MensalidadeResponse>("/mensalidades", body),
  update: (id: number, body: MensalidadeUpdate) => api.put<MensalidadeResponse>(`/mensalidades/${id}`, body),
  pagar: (id: number, body: MensalidadePagamento) => api.put<MensalidadeResponse>(`/mensalidades/${id}/pagar`, body)
};
