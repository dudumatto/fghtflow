import { api } from "./api";
import type { ApiPage, AulaResponse, AulaTipo, PresencaAulaResponse, PresencaAulaStatus } from "./types";

export type AulaCreate = {
  tipo: AulaTipo;
  titulo: string;
  descricao?: string | null;
  dataHoraInicio: string; // ISO
  dataHoraFim: string; // ISO
  capacidade?: number | null;
};

export type AulaUpdate = Partial<AulaCreate> & {
  ativa?: boolean;
};

export type PresencaCreate = {
  alunoId: number;
  status: PresencaAulaStatus;
};

export type PresencaUpdate = PresencaCreate;

export const aulasService = {
  list: (params: {
    dateFrom?: string;
    dateTo?: string;
    tipo?: AulaTipo;
    ativa?: boolean;
    professorUsuarioId?: number;
    q?: string;
    page?: number;
    size?: number;
  } = {}) => {
    const qs = new URLSearchParams();
    qs.set("page", String(params.page ?? 0));
    qs.set("size", String(params.size ?? 20));
    qs.set("sort", "dataHoraInicio,desc");
    if (params.dateFrom) qs.set("dateFrom", params.dateFrom);
    if (params.dateTo) qs.set("dateTo", params.dateTo);
    if (params.tipo) qs.set("tipo", params.tipo);
    if (params.ativa !== undefined) qs.set("ativa", String(params.ativa));
    if (params.professorUsuarioId !== undefined) qs.set("professorUsuarioId", String(params.professorUsuarioId));
    if (params.q) qs.set("q", params.q);
    return api.get<ApiPage<AulaResponse>>(`/aulas?${qs.toString()}`);
  },
  create: (body: AulaCreate) => api.post<AulaResponse>("/aulas", body),
  update: (id: number, body: AulaUpdate) => api.put<AulaResponse>(`/aulas/${id}`, body),
  presencas: {
    list: (aulaId: number) => api.get<PresencaAulaResponse[]>(`/aulas/${aulaId}/presencas`),
    create: (aulaId: number, body: PresencaCreate) =>
      api.post<PresencaAulaResponse>(`/aulas/${aulaId}/presencas`, body),
    update: (aulaId: number, body: PresencaUpdate) =>
      api.put<PresencaAulaResponse>(`/aulas/${aulaId}/presencas`, body)
  }
};
