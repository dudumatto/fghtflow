# Projeto: FightFlow (local bridge)

Este arquivo e **minimalista** por design. Use o sistema global do Codex em `C:\\Users\\Administrator\\.codex` (agents/rules/skills/workflows) e carregue apenas o contexto necessario para a tarefa.

## Prioridade de Fonte (ordem)
1. `.codex` global (`C:\\Users\\Administrator\\.codex`) = fonte canônica para agents/rules/skills/workflows e semantic routing.
2. Contexto do projeto (este repo: `backend/`, `frontend/`, `README.md`, etc.) = apenas o necessário para a tarefa atual.

## Stack
- Backend: Java 21, Spring Boot, Spring Security (JWT + RBAC + anti-IDOR), JPA/Hibernate, PostgreSQL
- Frontend: React, Vite, Tailwind CSS

## Estrutura
- `backend/` API Spring Boot (controller -> service -> repository -> dto)
- `frontend/` app React (services/state/pages/components)

## Comandos Principais
Backend:
- `cd backend`
- `mvn test`
- `mvn spring-boot:run`

DB (dev):
- `docker compose up -d`

Frontend:
- `cd frontend`
- `npm run dev`
- `npm run build`

## Arquivos Importantes
- `README.md` (setup local e contrato geral)
- `TEST_REPORT.md` (auditoria e pendencias)
- `backend/src/main/resources/application.yml` (DB_* + JWT_* + uploads)
- `docker-compose.yml` (PostgreSQL dev)
- `frontend/src/services/api.ts` (VITE_API_BASE + refresh token flow)

## Partes Sensiveis / Fragis
- Auth e refresh token (cookie HttpOnly `ff_refresh`)
- Anti-IDOR (ownership/escopo em services)
- Upload (10MB, MIME+ext allowlist, preview PDF only, 413/415)
- Paginacao/filtros em list endpoints (Pageable)
- JPA lazy loading: evitar acesso fora de transacao (preferir fetch explicit / projections)

## Contexto Local (minimo)
- `PROJECT_CONTEXT.md` (crie/atualize apenas quando houver decisoes de arquitetura do projeto)
- `MEMORY.md` (curto: bugs + solucoes + comandos de validacao; evitar historico longo)

## Regras de Uso (anti-waste)
- Preferir `rg` para localizar codigo antes de ler arquivos inteiros.
- Carregar so as skills globais relevantes ao problema.
- Evitar duplicar regras/workflows: a fonte de verdade e a `.codex` global.
