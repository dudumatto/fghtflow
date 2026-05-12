import { api } from "./api";
import type { AlunoPayload, AlunoResponse, SelectOptionResponse } from "./types";

export const alunosService = {
  list: () => api.get<AlunoResponse[]>("/alunos"),
  select: () => api.get<SelectOptionResponse[]>("/alunos/select"),
  create: (body: AlunoPayload) => api.post<AlunoResponse>("/alunos", body),
  update: (id: number, body: AlunoPayload) => api.put<AlunoResponse>(`/alunos/${id}`, body),
  remove: (id: number) => api.del<void>(`/alunos/${id}`)
};
