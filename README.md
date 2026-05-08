# FightFlow

Production-ready SaaS for Jiu-Jitsu academies: athletes, training sessions, competitions, fights, and performance analytics.

## Local Setup (Recommended)

### 1) PostgreSQL (Docker)

Single recommended credential set (dev):
- `POSTGRES_DB=fightflow`
- `POSTGRES_USER=fightflow`
- `POSTGRES_PASSWORD=fightflow`

Option A (docker run):
```bash
docker rm -f fightflow-pg
docker run --name fightflow-pg -e POSTGRES_DB=fightflow -e POSTGRES_USER=fightflow -e POSTGRES_PASSWORD=fightflow -p 5432:5432 -d postgres:16
```

Option B (docker compose):
```bash
docker compose up -d
```

### 2) Backend

Config via env vars (see `backend/.env.example`):
- `DB_URL=jdbc:postgresql://localhost:5432/fightflow`
- `DB_USER=fightflow`
- `DB_PASSWORD=fightflow`

Run:
```bash
cd backend
mvn spring-boot:run
```

### 3) Frontend

Set API base URL (see `frontend/.env.example`):
- `VITE_API_BASE=http://localhost:8080`

Run:
```bash
cd frontend
npm install
npm run dev
```

## Tech

Backend:
- Java 21
- Spring Boot + Spring Security (JWT + RBAC)
- JPA/Hibernate + PostgreSQL

Frontend:
- React + Vite
- Tailwind CSS

## Architecture (High Level)

Backend base package: `backend/src/main/java/com/fightflow`
- `controller/`: REST endpoints (thin)
- `service/`: business rules + authorization/ownership checks (anti-IDOR)
- `repository/`: JPA repositories + Specifications
- `security/`: JWT, stateless filter chain, REST auth handlers
- `exception/`: global error mapping and envelopes
- `dto/`: request/response contracts
- `entity/`: JPA entities
- `util/`: shared helpers (paging, upload rules, token utils)

Frontend:
- `frontend/src/services/`: API client (response envelope unwrap + auto refresh retry)
- `frontend/src/state/`: auth state
- `frontend/src/pages/`: Login, Dashboard, Perfil, Lutas, Competicoes, Treinos
- `frontend/src/components/`: UI primitives + skeletons

## Testing Notes (Windows)

If PowerShell blocks `npm` scripts (`running scripts is disabled`), run via `cmd`:

```bat
cd frontend
cmd /c "npm.cmd run build"
```

Typecheck only:

```bat
cd frontend
cmd /c "node_modules\\.bin\\tsc.cmd -b"
```

## Security

- JWT access token for API calls (RBAC: `ATLETA`, `PROFESSOR`, `ADMIN`)
- Refresh token rotation via HttpOnly cookie (`ff_refresh`)
- Anti-IDOR: server-side ownership checks (athlete can only access own data; professor scoped to academy)
- Secure upload rules:
  - max 10MB
  - allowed: PDF/DOC/DOCX (MIME + extension allowlist)
  - storage: UUID filename under `uploads/{usuarioId}/`
  - access: owner-only
  - preview: PDF only

## API Contract

### Global response envelope

Success:
```json
{ "success": true, "data": {} }
```

Error:
```json
{ "success": false, "error": "Forbidden" }
```

### Pagination + filters

List endpoints accept `page`, `size`, `sort` plus filters:
```http
GET /lutas?page=0&size=20&sort=foughtAt,desc&atletaId=1&resultado=WIN&dateFrom=2026-01-01T00:00:00Z&dateTo=2026-12-31T23:59:59Z
GET /competicoes?page=0&size=20&sort=startsAt,desc&dateFrom=2026-01-01T00:00:00Z
GET /treinos?page=0&size=20&sort=startsAt,desc
```

### Auth (access + refresh)

Login/Register returns the access token in the response body and sets a refresh cookie:
```http
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout
```
