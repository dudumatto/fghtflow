# FightFlow

Production-ready SaaS for Jiu-Jitsu academies: athletes, training sessions, competitions, fights, and performance analytics.

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
{ "success": false, "error": { "status": 403, "message": "Forbidden", "path": "/..." } }
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

