# FightFlow E2E Report

Data: 2026-05-10

## Resultado

- Comando: `npx playwright test`
- Resultado final: 20 passed
- Navegador: Chromium
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Relatorio HTML: `frontend/playwright-report/index.html`
- Artefatos em falha: `frontend/test-results/`

## Cobertura Criada

- Auth: rota protegida, login invalido, login valido, logout, refresh token, persistencia de sessao.
- RBAC/anti-IDOR: ADMIN em dashboard admin, PROFESSOR impedido de alterar aula de outro professor, ATLETA impedido de acessar dados de outro atleta, ATLETA impedido de acessar dashboard admin.
- Aulas: criar pela UI, listar, filtrar, paginacao basica, editar por API, excluir/desativar por API e refletir na UI.
- Presencas: registrar pela UI, impedir duplicada, atleta ver apenas propria presenca via API, impedir presenca PRESENTE para aluno bloqueado.
- Uploads: PDF valido, preview PDF, arquivo invalido 415, arquivo maior que 10MB 413.
- Dashboard: cards de admin e atleta carregam.
- Frontend: console errors criticos e network 5xx no fluxo principal.
- Responsividade: mobile, tablet e desktop sem overflow horizontal em fluxo principal.

## Correcoes Aplicadas

- Professor/admin agora sao redirecionados para `/dashboard/admin` apos login/cadastro.
- A rota `/dashboard` redireciona professor/admin para `/dashboard/admin`, evitando chamada indevida a `/dashboard/atleta`.
- Login ganhou `autocomplete` em email/senha.
- Adicionado `public/favicon.svg` para eliminar 404 de favicon.
- Backend agora trata `MaxUploadSizeExceededException` como 413 `File too large`.

## Observacoes

- A aplicacao nao possui tela de upload dedicada; a cobertura de upload foi feita via API usando `@playwright/test`.
- A UI de aulas ainda nao expoe editar/excluir diretamente; esses fluxos foram cobertos por API e validacao visual na listagem.
- `playwright.config.ts` grava screenshots, videos e traces em falhas.

---

## Atualizacao 2026-05-14

- Estrutura E2E reorganizada para `frontend/e2e`.
- Specs centralizados em `frontend/e2e/tests`.
- Page Objects centralizados em `frontend/e2e/pages`.
- Helpers centralizados em `frontend/e2e/helpers`.
- Fixture compartilhada criada em `frontend/e2e/fixtures/auth.fixture.ts`.
- Relatorio manual movido para `frontend/e2e/reports/E2E_REPORT.md`.
- `playwright.config.ts` mantido na raiz do `frontend` com `testDir: ./e2e/tests`.

## Resultado atual

- Comando: `npm run test:e2e`
- Resultado: 5 passed, 23 skipped
- Motivo dos skips: testes integrados preservados dependem de backend/DB prontos e exigem `E2E_RUN_BACKEND=true`.
- Smokes executados: login abre, rota protegida redireciona, login invalido exibe erro, menu respeita ALUNO, rota proibida nao exibe 500.
