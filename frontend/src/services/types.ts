export type Role = "ATLETA" | "PROFESSOR" | "ADMIN";

export type ApiPage<T> = {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
  hasNext: boolean;
  hasPrev: boolean;
};

export type AuthResponse = {
  token: string;
  usuarioId: number;
  role: Role;
  academiaId: number | null;
};

export type AtletaProfileResponse = {
  atletaId: number;
  usuarioId: number;
  academiaId: number | null;
  email: string;
  faixa: string | null;
  peso: number | null;
  categoria: string | null;
};

export type AtletaDashboardResponse = {
  totalFights: number;
  wins: number;
  losses: number;
  winrate: number;
  submissionRate: number;
};

export type TreinoResponse = {
  id: number;
  startsAt: string;
  titulo: string;
  descricao: string | null;
};

export type CompeticaoResponse = {
  id: number;
  nome: string;
  local: string | null;
  startsAt: string;
};

export type LutaResponse = {
  id: number;
  atletaId: number;
  competicaoId: number | null;
  adversarioNome: string | null;
  resultado: "WIN" | "LOSS";
  metodo: "SUBMISSION" | "POINTS" | "DECISION" | "OTHER";
  foughtAt: string;
};

export type DocumentoResponse = {
  id: number;
  originalName: string;
  mimeType: string;
  sizeBytes: number;
  createdAt: string;
};
