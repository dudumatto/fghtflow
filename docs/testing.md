# Testes

## Backend

```bash
cd backend
mvn test
```

Em alguns ambientes Windows/sandbox, pode ser necessario apontar o repositorio Maven local:

```powershell
mvn "-Dmaven.repo.local=C:\Users\Administrator\Desktop\fullstack\backend\.m2\repository" test
```

## Frontend

```bash
cd frontend
npm install
npm run build
```

Se o PowerShell bloquear scripts npm, use `cmd`:

```bat
cd frontend
cmd /c "npm.cmd run build"
```

Typecheck isolado:

```bat
cd frontend
cmd /c "node_modules\\.bin\\tsc.cmd -b"
```

## E2E

Os testes Playwright ficam em `frontend/e2e`.

```bash
cd frontend
npm run test:e2e
npm run test:e2e:headed
npm run test:e2e:ui
npm run test:e2e:report
```

Mais detalhes:

- [Guia E2E](../frontend/e2e/README.md)
- [Relatorio E2E](../frontend/e2e/reports/E2E_REPORT.md)
- [Relatorio historico de testes](../ai/TEST_REPORT.md)
