import { api } from "./api";
import type { AcademiaResponse, AcademiaResumoResponse } from "./types";

export type AcademiaPayload = {
  nome: string;
  endereco?: string | null;
  ativo?: boolean;
  professorResponsavelId?: number | null;
};

export const academiasService = {
  list: () => api.get<AcademiaResponse[]>("/academias"),
  select: () => api.get<AcademiaResumoResponse[]>("/academias/select"),
  create: (body: AcademiaPayload) => api.post<AcademiaResponse>("/academias", body),
  update: (id: number, body: AcademiaPayload) => api.put<AcademiaResponse>(`/academias/${id}`, body),
  remove: (id: number) => api.del<void>(`/academias/${id}`)
};
