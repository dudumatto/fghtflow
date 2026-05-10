# Memoria do Projeto

## Bugs importantes
- (Registrar aqui somente bugs recorrentes ou que voltam a aparecer em ambiente local/CI.)

## Solucoes reutilizaveis
- Para evitar `LazyInitializationException` em endpoints de perfil: preferir repository query com `join fetch`/`@EntityGraph`/projection + service `@Transactional(readOnly = true)` e mapear para DTO dentro da transacao.
- Para erros previsiveis de cadastro/login: retornar status controlado (400/401/409) via handler; evitar 500.
- Presenca em `Aula`: impedir duplicidade por `(aula_id, aluno_id)` (unique constraint + checagem no service) e barrar `PRESENTE` para aluno BLOQUEADO via `FinanceiroBloqueioService.assertPodeRegistrarPresenca`.
- Graduacao/Evolucao: para historico por aluno, aplicar anti-IDOR por role (ATLETA so o proprio `alunoId`; PROFESSOR/ADMIN apenas mesma academia) e exigir `observacao` quando graduacao nao segue proximo passo esperado (mesma faixa grau+1 ou proxima faixa grau=0).
- Maven local (ambiente Codex/Windows): se `mvn test` falhar com `LocalRepositoryNotAccessibleException` tentando usar `C:\\Users\\CodexSandboxOffline\\.m2`, rode com `-Dmaven.repo.local=C:\\Users\\Administrator\\Desktop\\fullstack\\backend\\.m2\\repository`.

## Decisoes arquiteturais
- Controller -> Service -> Repository -> DTO (nao expor entities).
- Padrao de resposta (envelope): `{ success, data }` / `{ success, error }`.
- Modelagem academia: usar `Aluno` para gestao administrativa/financeira/aulas; manter `Atleta` para performance/lutas/dashboard.
- Financeiro: `Plano`, `Matricula` e `Mensalidade` devem sempre ser filtrados por academia/usuario no service; nunca confiar em `alunoId` do cliente sem checar ownership.
- Ao criar `Matricula`, gerar automaticamente a primeira `Mensalidade` com valor do plano, vencimento em `dataInicio`, status `PENDENTE` e referencia `matricula:{id}:primeira`.
- Bloqueio por inadimplencia: `FinanceiroBloqueioService` bloqueia `Matricula ATIVA -> BLOQUEADA` quando ha mensalidade `PENDENTE/ATRASADO` vencida alem da tolerancia; pagamento/cancelamento regulariza `BLOQUEADA -> ATIVA` se nao restar divida bloqueante.
- Transicoes financeiras que alteram status de `Matricula` devem usar lock/indices por `(aluno_id,status)`; consultas de inadimplencia usam `(aluno_id,status,vencimento)`.

## Integracoes importantes
- PostgreSQL dev via Docker (credenciais padronizadas no `README.md` / `.env.example` quando existirem).
- Refresh token em cookie HttpOnly `ff_refresh` (fluxo: login -> refresh -> logout).
- Upload seguro em disco por usuario: `/uploads/{usuarioId}/` + UUID; allowlist + limites.

## Learnings
- Evitar documentacao duplicada: `.codex` global e canonica; manter `PROJECT_CONTEXT.md`/`MEMORY.md` curtos e atualizados.
- Task 8 (2026-05-08): `mvn test` passou usando `-Dmaven.repo.local=C:\\Users\\Administrator\\Desktop\\fullstack\\backend\\.m2\\repository`. No mesmo ambiente Windows, `npm run build` (Vite) falha com `spawn EPERM` do `esbuild --service` (repro: `node -e "require('esbuild').transformSync(...)"`), entao validar frontend por `tsc -b` e documentar em `TEST_REPORT.md`. Recomendar ignorar `frontend/tsconfig.tsbuildinfo` no `.gitignore`.

## Problemas recorrentes
- Mismatch de credenciais/config DB entre ambientes (Docker vs `application.yml` vs env vars).
- Env var do frontend (`VITE_API_BASE`) divergente do que esta documentado.

## Proximos cuidados
- Sempre testar cenarios de seguranca: sem token, token invalido, role denial, IDOR, upload 10MB+, preview nao-PDF.
- Ao adicionar endpoints de listagem: garantir `Pageable` + filtros + sort sem vazar dados entre usuarios/academias.

## 🔗 Relacionamentos
- `PROJECT_CONTEXT.md` referencia partes sensiveis e integracoes; `TEST_REPORT.md` guarda estado de auditoria/pedencias.
