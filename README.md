# FightFlow

SaaS fullstack para academias de lutas gerenciarem alunos, atletas, treinos, competicoes, lutas, mensalidades e indicadores de performance.

## Stack

- Backend: Java 21, Spring Boot, Spring Security, JPA/Hibernate, PostgreSQL
- Frontend: React, Vite, TypeScript, Tailwind CSS
- Testes: Maven, Playwright

## Estrutura

- `backend/`: API Spring Boot
- `frontend/`: aplicacao React/Vite
- `docs/`: documentacao tecnica
- `ai/`: contexto, memoria e rotina de IA
- `scripts/`: automacoes locais
- `.github/workflows/`: CI

## Documentacao

- [Setup local](docs/setup.md)
- [Arquitetura](docs/architecture.md)
- [Seguranca](docs/security.md)
- [API](docs/api.md)
- [Testes](docs/testing.md)

## Comandos rapidos

```powershell
.\scripts\dev.ps1 -Service db
.\scripts\dev.ps1 -Service backend
.\scripts\dev.ps1 -Service frontend
.\scripts\test-all.ps1
```

## IA e memoria

- [AGENTS](ai/AGENTS.md)
- [Memoria](ai/MEMORY.md)
- [Contexto do projeto](ai/PROJECT_CONTEXT.md)
- [Rotina operacional](ai/ROTINA_IA.md)
