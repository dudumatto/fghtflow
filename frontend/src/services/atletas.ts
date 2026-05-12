import { api } from "./api";
import type { AtletaPayload, AtletaResponse, SelectOptionResponse } from "./types";

export const atletasService = {
  list: () => api.get<AtletaResponse[]>("/atletas"),
  select: () => api.get<SelectOptionResponse[]>("/atletas/select"),
  create: (body: AtletaPayload) => api.post<AtletaResponse>("/atletas", body),
  update: (id: number, body: AtletaPayload) => api.put<AtletaResponse>(`/atletas/${id}`, body),
  remove: (id: number) => api.del<void>(`/atletas/${id}`)
};
