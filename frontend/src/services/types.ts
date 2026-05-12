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
  academiaNome: string | null;
};

export type AcademiaResponse = {
  id: number;
  nome: string;
  endereco: string | null;
  ativo: boolean;
  professorResponsavelId: number | null;
  professorResponsavelNome: string | null;
  createdAt: string;
  updatedAt: string;
};

export type AcademiaResumoResponse = {
  id: number;
  nome: string;
};

export type SelectOptionResponse = {
  id: number;
  nome: string;
};

export type AlunoResponse = {
  id: number;
  usuarioId: number;
  nome: string;
  email: string;
  role: Role;
  academiaId: number;
  academiaNome: string;
  ativo: boolean;
  faixaAtual: string | null;
  grauAtual: number;
  createdAt: string;
};

export type AlunoPayload = {
  email?: string;
  password?: string;
  nome?: string;
  academiaId?: number;
  ativo?: boolean;
  faixaAtual?: string | null;
  grauAtual?: number;
};

export type AtletaResponse = {
  id: number;
  usuarioId: number;
  alunoId: number | null;
  nome: string;
  email: string;
  role: Role;
  academiaId: number;
  academiaNome: string;
  ativo: boolean;
  faixa: string | null;
  grauAtual: number;
  peso: number | null;
  categoria: string | null;
  createdAt: string;
};

export type AtletaPayload = {
  email?: string;
  password?: string;
  nome?: string;
  academiaId?: number;
  ativo?: boolean;
  faixa?: string | null;
  grauAtual?: number;
  peso?: number | null;
  categoria?: string | null;
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

// --- Task 7 (novos modulos) ---

export type PlanoResponse = {
  id: number;
  academiaId: number;
  nome: string;
  descricao: string | null;
  valor: number;
  duracaoEmDias: number;
  ativo: boolean;
  createdAt: string;
};

export type MensalidadeStatus = "PENDENTE" | "ATRASADO" | "PAGO" | "CANCELADO";
export type MetodoPagamento = "PIX" | "CARTAO" | "BOLETO" | "DINHEIRO" | "MANUAL";

export type MensalidadeResponse = {
  id: number;
  alunoId: number;
  planoId: number;
  valor: number;
  vencimento: string;
  dataPagamento: string | null;
  status: MensalidadeStatus;
  metodoPagamento: MetodoPagamento | null;
  referencia: string | null;
  createdAt: string;
};

export type AulaTipo = "COLETIVA" | "PARTICULAR";

export type AulaResponse = {
  id: number;
  professorUsuarioId: number;
  titulo: string;
  descricao: string | null;
  tipo: AulaTipo;
  dataHoraInicio: string;
  dataHoraFim: string;
  capacidade: number | null;
  ativa: boolean;
};

export type PresencaAulaStatus = "PRESENTE" | "AUSENTE" | "JUSTIFICADO";

export type PresencaAulaResponse = {
  id: number;
  alunoId: number;
  status: PresencaAulaStatus;
  registradaEm: string;
};

export type GraduacaoResponse = {
  id: number;
  alunoId: number;
  faixa: string;
  grau: number;
  observacao: string | null;
  dataGraduacao: string;
  professorUsuarioId: number;
  foraDeOrdem: boolean;
};

export type EvolucaoAlunoResponse = {
  id: number;
  alunoId: number;
  tipo: string;
  descricao: string;
  data: string;
  professorUsuarioId: number;
};

export type AdminDashboardResponse = {
  alunosAtivos: number;
  usuariosAtletas: number;
  usuariosProfessores: number;
  usuariosAdmins: number;
  planosAtivos: number;
  matriculasAtivas: number;
  matriculasBloqueadas: number;
  aulasProximas: number;
  generatedAt: string;
};

export type FinanceiroDashboardResponse = {
  mensalidadesPendentes: number;
  mensalidadesAtrasadas: number;
  mensalidadesPagasNoMes: number;
  totalPendente: number | null;
  totalAtrasado: number | null;
  receitaNoMes: number | null;
  alunosComInadimplenciaBloqueante: number;
  diasToleranciaInadimplencia: number;
  generatedAt: string;
};

export type AlunoInadimplenciaResumo = {
  alunoId: number;
  nome: string;
  totalAberto: number;
  mensalidadesEmAberto: number;
  oldestVencimento: string | null;
};

export type AlunosDashboardResponse = {
  alunosAtivos: number;
  alunosNovos30d: number;
  alunosComInadimplenciaBloqueante: number;
  topInadimplentes: AlunoInadimplenciaResumo[];
  generatedAt: string;
};

export type EvolucaoDashboardResponse = {
  alunoId: number;
  faixaAtual: string | null;
  grauAtual: number;
  ultimaGraduacaoEm: string | null;
  totalEvolucoes: number;
  recomendacoes: string[];
};
