# FightFlow E2E

## Estrutura

- `fixtures/`: fixtures Playwright compartilhadas.
- `helpers/`: auth/API, rotas, dados de teste e assertions.
- `pages/`: Page Objects do frontend.
- `tests/`: specs por dominio.
- `reports/`: relatorios manuais, incluindo `E2E_REPORT.md`.

## Como rodar

```powershell
npm run test:e2e
npm run test:e2e:headed
npm run test:e2e:ui
npm run test:e2e:debug
npm run test:e2e:report
```

O `playwright.config.ts` fica na raiz do `frontend/` e usa `testDir: ./e2e/tests`.

## Variaveis

- `E2E_UI_BASE`: URL do Vite. Padrao: `http://127.0.0.1:5173`.
- `E2E_API_BASE`: URL do backend. Padrao: `http://localhost:8080`.
- `E2E_RUN_BACKEND=true`: habilita testes integrados que criam dados via API.
- `E2E_EMAIL` e `E2E_PASSWORD`: habilitam smoke de login/dashboard com usuario real.
- `E2E_ROLE`: role do usuario real, padrao `ATLETA`.

Sem essas variaveis, os testes que dependem de login real ou backend preparado devem usar `test.skip` com mensagem clara.

## Padrao POM

Crie interacoes de tela em `e2e/pages/*Page.ts` e mantenha as specs focadas no comportamento. Helpers de API, rotas, dados e assertions ficam em `e2e/helpers/`.

## Novos testes

Use `e2e/tests/<dominio>/<dominio>.spec.ts`. Para testes que dependem de backend/DB, use `shouldRunBackendE2E()` e `backendE2ESkipMessage`.

## Relatorios

Artefatos gerados ficam em `test-results/` e `playwright-report/`, fora do versionamento. O relatorio manual fica em `e2e/reports/E2E_REPORT.md`.
