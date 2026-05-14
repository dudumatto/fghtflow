# Contexto do Projeto

## Objetivo
- FightFlow: SaaS para academias gerenciarem atletas, treinos, competicoes, lutas e documentos, com dashboard de performance.

## Stack
- Backend: Java 21, Spring Boot, Spring Security (JWT + RBAC + anti-IDOR), JPA/Hibernate, PostgreSQL
- Frontend: React, Vite, Tailwind CSS
- Infra local: PostgreSQL via Docker (ver `docker-compose.yml` e `docs/setup.md`)

## Arquitetura
- Backend em camadas: controller -> service -> repository -> dto
- Response envelope: `{ success: true, data }` / `{ success: false, error }`
- Seguranca:
  - Access token (JWT) + refresh token (cookie HttpOnly `ff_refresh`)
  - RBAC: ALUNO, ATLETA, PROFESSOR, ADMIN
  - Anti-IDOR: validacao de ownership/escopo no service (nao expor entity)

## Estrutura principal
- `backend/` API Spring Boot
- `frontend/` app React
- `docker-compose.yml` Postgres dev
- `README.md` indice curto do projeto
- `docs/setup.md` setup e comandos
- `ai/TEST_REPORT.md` status de auditoria e pendencias

## Integracoes
- PostgreSQL (dev via Docker)
- Upload seguro em disco:
  - `/uploads/{usuarioId}/` + nome UUID
  - max 10MB, allowlist PDF/DOC/DOCX (MIME + extensao)
  - preview somente PDF (415 se invalido)

## Partes sensiveis
- Auth/refresh token (cookie HttpOnly, expiracao, logout)
- CORS + `VITE_API_BASE` no frontend
- Upload/preview (413/415/403)
- Anti-IDOR (todas rotas com `usuarioId`/`atletaId` devem validar ownership)
- JPA lazy loading: evitar acesso fora de transacao (usar fetch/projection quando necessario)

## Decisoes importantes
- `.codex` global (`C:\Users\Administrator\.codex`) e a fonte canonica de agents/rules/skills/workflows/routing; este repo mantem apenas contexto local minimo.
- Preferir mudancas pequenas e compatibilidade (nao reescrever arquitetura).
- `Aluno` e o cadastro administrativo base da academia; `Atleta` fica como perfil esportivo/performance ligado opcionalmente a `Aluno`.
- `Plano` e escopado por academia para cumprir RBAC/anti-IDOR financeiro; criacoes devem receber academia pelo usuario autenticado.
- Bloqueio financeiro usa `FINANCEIRO_DIAS_TOLERANCIA_INADIMPLENCIA`: mensalidade pendente/atrasada alem da tolerancia bloqueia matriculas ativas e impede presenca.

## Proximos passos
- Validar end-to-end: competicoes, lutas, treinos, upload, paginacao/filtros e cenarios IDOR.
- Revalidar `npm run build` localmente e documentar env vars do frontend (especialmente `VITE_API_BASE`).

## 🔗 Relacionamentos
- Backend <-> Frontend: `frontend/src/services/api.ts` consome API; base URL por `VITE_API_BASE`.
- Seguranca: controllers dependem do security context; services fazem enforcement de ownership/escopo.
- Documentos: metadados em DB + arquivo em disco por usuario; acesso sempre verificado por ownership.
