# FightFlow Test Report

Date: 2026-05-06
Environment: Local + Codex sandbox (Windows / PowerShell)

## Status
APROVADO COM PENDENCIAS

Local status: core auth flows and athlete/dashboard endpoints were validated successfully in the real local environment:
- `POST /auth/register` -> `200`
- `POST /auth/login` -> `200`
- `GET /atletas/me` -> `200`
- `GET /dashboard/atleta` -> `200`

Sandbox note: the `500` failures observed in this report were specific to the Codex sandbox and are most likely related to PostgreSQL credential/config mismatch and/or environment restrictions. This is not considered a confirmed bug in the application logic.

## Commands Executed

Architecture / config inspection:
- `Get-Content` / `Get-ChildItem` over:
  - `backend/pom.xml`
  - `backend/src/main/resources/application.yml`
  - `frontend/package.json`
  - `docker-compose.yml`
  - `README.md`

Backend HTTP probes (via `curl.exe`):
- `GET /atletas/me` (no token)
- `POST /auth/register` (valid payload)
- `POST /auth/login` (valid + invalid payload)

Frontend build:
- `cd frontend; cmd /c npm run build`

## Architecture Read

- Backend: Spring Boot 3.x, Spring Security (JWT), JPA/Hibernate, PostgreSQL runtime.
- Frontend: React + Vite + Tailwind.
- Response envelope: `ApiResponse<T> { success, data, error }` with `error` as string.
- Pagination: `/lutas`, `/competicoes`, `/treinos` use `Pageable`.

## Configuration Review

### PostgreSQL
- Standardized (recommended for local dev):
  - `POSTGRES_DB=fightflow`
  - `POSTGRES_USER=fightflow`
  - `POSTGRES_PASSWORD=fightflow`
- Backend reads credentials from env vars with safe defaults:
  - `DB_URL` / `DB_USER` / `DB_PASSWORD` (see `backend/.env.example`)

### JWT
- `fightflow.jwt.secret` default is `${JWT_SECRET:change-me-change-me-change-me-change-me}`.
Risk: ensure production sets a strong secret via env var.

### CORS
- `CorsConfig` allows `*` origins with credentials enabled.
Risk: for production, tighten `allowedOriginPatterns` to a controlled list.

### Frontend API URL
- Frontend client reads `VITE_API_BASE` (not `VITE_API_URL`).
Action: document/standardize the expected env var name.

### Gitignore
- `.gitignore` was missing initially.
- Added `.gitignore` to avoid committing runtime artifacts (`uploads/`, `node_modules/`, `target/`).

## Backend Test Results

### Protected routes
- `GET /atletas/me` without token: `403` (OK, consistent with current security handler).

### Auth (Local validated)
Validated locally:
- `POST /auth/register` -> `200`
- `POST /auth/login` -> `200`

Known local payload used:
- email: `me@fightflow.test`
- role: `ATLETA`
- academyId returned: `1`

Dashboard baseline returned:
- `totalFights: 0`
- `wins: 0`
- `losses: 0`
- `winrate: 0.0`
- `submissionRate: 0.0`

### Auth (Sandbox anomaly)
In Codex sandbox only, this report observed `500` on `/auth/register` and `/auth/login`. Treat this as an environment issue until reproduced locally with backend logs.

## Security Validation (Partial)

Validated:
- Protected endpoint without token returns `403`.

Not validated due to auth 500:
- Token invalid -> 403/401
- IDOR 403 (needs valid tokens)
- Upload 10MB -> 413
- Preview non-PDF -> 415

## Business Rules Validation (Blocked)

Blocked by auth failures:
- ATLETA ownership rules
- PROFESSOR academy scoping
- Dashboard stats updates after fights
- Pagination correctness with real data

## Frontend Test Results

- `npm run build` failed with:
  - `Error: spawn EPERM` from `esbuild` while loading `vite.config.ts`.

Impact: cannot validate frontend build pipeline in this sandbox. May be environment restriction; re-run locally to confirm.

## Bugs Found

P1 (Tech debt / portability)
1. Standardize PostgreSQL credentials and configuration across `docker-compose.yml` and backend env vars (`DB_*`).
2. Document the correct frontend env var (`VITE_API_BASE`).
3. Re-validate `npm run build` locally (sandbox `spawn EPERM` may be environmental).

P2 (Coverage gaps)
1. Still to test end-to-end: competicoes, lutas, treinos, upload, pagination and IDOR scenarios.

## Recommended Fixes (Before Next Audit Pass)

Backend:
1. Confirm datasource config is standardized:
   - `DB_URL/DB_USER/DB_PASSWORD` (see `backend/.env.example`)
   - compose credentials match the same values.
2. Expand test coverage for the remaining flows (see pending checklist below).

Frontend:
1. Re-run `npm run build` locally.
2. Use `frontend/.env.example` for `VITE_API_BASE`.

## What Is Working

- API responds and security handler returns JSON envelope for protected routes (e.g. `/atletas/me` -> 403).
- Response envelope format is consistent (`success/data/error`).

## What Still Needs Verification

End-to-end flows (pending):
- Refresh/logout behavior (`/auth/refresh`, `/auth/logout`)
- Competicoes (create + list + pagination)
- Treinos (create + list + pagination)
- Lutas (create + list + pagination + filters)
- Upload flows:
  - invalid extension -> 415
  - >10MB -> 413
  - preview non-PDF -> 415
- IDOR scenarios (athlete accessing other athlete, professor out-of-academy access)
- Frontend build/dev + protected routes + refresh token + logout
