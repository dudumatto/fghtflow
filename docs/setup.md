# Setup Local

## Requisitos

- Java 21
- Maven
- Node.js
- npm
- Docker ou PostgreSQL local

## PostgreSQL

Credenciais recomendadas para desenvolvimento:

- `POSTGRES_DB=fightflow`
- `POSTGRES_USER=fightflow`
- `POSTGRES_PASSWORD=fightflow`

Com Docker:

```bash
docker rm -f fightflow-pg
docker run --name fightflow-pg -e POSTGRES_DB=fightflow -e POSTGRES_USER=fightflow -e POSTGRES_PASSWORD=fightflow -p 5432:5432 -d postgres:16
```

Ou via compose:

```bash
docker compose up -d
```

## Backend

Variaveis esperadas:

- `DB_URL=jdbc:postgresql://localhost:5432/fightflow`
- `DB_USER=fightflow`
- `DB_PASSWORD=fightflow`

Executar:

```bash
cd backend
mvn spring-boot:run
```

## Frontend

Variavel esperada:

- `VITE_API_BASE=http://localhost:8080`

Executar:

```bash
cd frontend
npm install
npm run dev
```

## Scripts locais

```powershell
.\scripts\dev.ps1 -Service db
.\scripts\dev.ps1 -Service backend
.\scripts\dev.ps1 -Service frontend
.\scripts\test-all.ps1
```
