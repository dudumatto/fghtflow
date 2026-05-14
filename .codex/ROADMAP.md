# FightFlow Roadmap

## Estado Atual

Sistema fullstack de gestão para academias/lutas.

Stack:
- Spring Boot
- React + Vite
- PostgreSQL
- JWT/RBAC
- Tailwind
- Playwright
- Maven

Arquitetura:
- controller -> service -> repository -> dto
- ApiResponse envelope
- anti-IDOR
- JWT + refresh token

---

# Waves

## Wave 1 — Academias
Status: ✅ Concluído

Implementado:
- CRUD de academias
- ProfessorAcademia
- AcademiaScopeService
- anti-IDOR por academia
- selects de academias
- frontend básico

Arquivo:
- `.codex/waves/WAVE_1_ACADEMIAS.md`

---

## Wave 2 — Alunos e Atletas
Status: ✅ Concluído

Implementado:
- CRUD de alunos
- CRUD de atletas
- selects reais
- vínculo por academia
- anti-IDOR
- professores vinculados

Arquivo:
- `.codex/waves/WAVE_2_ALUNOS_ATLETAS.md`

---

## Wave 3 — Permissões e Interface Dinâmica
Status: 🔄 Em andamento

Objetivo:
- menu por role
- rotas protegidas
- frontend não chamar APIs proibidas
- backend retornar 403 em vez de 500

Arquivo:
- `.codex/waves/WAVE_3_PERMISSOES.md`

---

## Wave 4 — Mensalidades
Status: ⏳ Pendente

Objetivo:
- professor gerencia mensalidades
- aluno apenas visualiza/paga
- histórico financeiro
- pendências

Arquivo:
- `.codex/waves/WAVE_4_MENSALIDADES.md`

---

## Wave 5 — Presença e Graduação
Status: ⏳ Pendente

Objetivo:
- presença apenas professor
- graduação apenas professor
- timeline visual de faixas/graus

Arquivo:
- `.codex/waves/WAVE_5_PRESENCA_GRADUACAO.md`

---

## Wave 6 — Dashboard Dinâmico
Status: ⏳ Pendente

Objetivo:
- dashboard por role
- widgets dinâmicos
- informações esportivas/financeiras

Arquivo:
- `.codex/waves/WAVE_6_DASHBOARD.md`

---

## Wave 7 — Aula Privada e Aula Experimental
Status: ⏳ Pendente

Objetivo:
- agendamento
- fluxo comercial
- calendário

Arquivo:
- `.codex/waves/WAVE_7_AULAS_PRIVADAS_TESTE.md`

---

## Wave 8 — Reconhecimento Facial e Catraca
Status: 💡 Futuro

Objetivo:
- presença automática
- biometria facial
- integração com catraca

---

# Regras Arquiteturais

- Não remover `Usuario.academia`
- Endpoints sem `/api`
- Sempre validar anti-IDOR
- Frontend nunca confiar apenas em role visual
- Backend sempre validar permissões
- Professor só acessa academias relacionadas
- ADMIN possui visão global

---

# Fluxo obrigatório para agentes

1. load-memory
2. ler roadmap
3. ler wave
4. implementar
5. rodar testes
6. learn-project
7. save-memory

---

# Comandos de memória

## Load

```powershell
powershell -ExecutionPolicy Bypass -File E:\second-brain\scripts\load-memory.ps1 -Project fightflow