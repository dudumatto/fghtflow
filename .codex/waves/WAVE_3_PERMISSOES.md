# WAVE 3 — Permissões e interface dinâmica

Objetivo:
- frontend não chamar APIs proibidas
- backend retornar 403 em vez de 500
- menus por role
- rotas protegidas

Problema atual:
- usuário ALUNO/ATLETA chama APIs administrativas
- várias rotas retornam 500

Implementar:
- permissions.ts
- canAccessRoute
- menu por role
- ProtectedRoute/RoleRoute

Corrigir:
- /graduacoes
- /evolucoes
- /dashboard/atleta
- /atletas/me
- /competicoes
- /treinos

Backend:
- retornar 403/404 corretos
- não deixar NPE virar 500

Frontend:
- esconder menus inválidos
- não chamar APIs proibidas

Executar:
- mvn test
- npm run build
- playwright/manual smoke

Salvar memória do projeto.