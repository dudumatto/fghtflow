# FightFlow — Frontend Architecture

## Stack

- React
- Vite
- TypeScript
- Tailwind CSS
- React Router
- API client centralizado

## Padrão obrigatório

- Não chamar API diretamente espalhada sem service.
- Criar service em `frontend/src/services`.
- Tipar requests/responses em `types.ts` ou arquivo específico.
- Componentes devem respeitar role/permissões.
- Menu deve ser gerado por role.

## Estrutura esperada

- pages: telas principais
- components: componentes reutilizáveis
- services: chamadas HTTP
- layout: AppShell/menu
- auth/context: usuário logado, token, role
- permissions: regras de acesso no frontend

## API client

O client deve:

- usar baseURL correta
- enviar token JWT
- lidar com refresh token
- entender envelope `{ success, data, error }`
- tratar 401 redirecionando login
- tratar 403 mostrando mensagem amigável
- não quebrar tela com erro 500

## Permissões no frontend

Criar/usar centralizador:

- `permissions.ts`
- `canAccessRoute(role, path)`
- `canUseFeature(role, feature)`
- `getMenuItemsByRole(role)`

## Menu lateral

O menu deve mudar por role.

ALUNO:
- Dashboard
- Mensalidades
- Agenda
- Graduação
- Perfil

ATLETA:
- Dashboard
- Mensalidades
- Agenda
- Graduação
- Evolução
- Lutas
- Competições
- Treinos
- Perfil

PROFESSOR:
- Dashboard
- Academias
- Alunos
- Atletas
- Mensalidades
- Agenda
- Presenças
- Graduação
- Evolução
- Aulas privadas
- Aulas experimentais

ADMIN:
- acesso global

## Rotas protegidas

Se usuário não tem permissão:

- não renderizar página
- não chamar API proibida
- redirecionar para dashboard ou exibir 403

## Formulários

Evitar IDs manuais.

Usar selects reais:

- academias
- alunos
- atletas
- professores
- planos
- aulas

## Interface por entidade

ALUNO:
- não gerencia mensalidade
- não gerencia presença
- não gerencia graduação
- não gerencia competição

ATLETA:
- pode acessar área esportiva
- não gerencia mensalidade
- não gerencia presença
- não gerencia graduação

PROFESSOR:
- gerencia alunos
- gerencia atletas
- gerencia mensalidades
- gerencia presença
- gerencia graduação

## Estados de tela

Toda tela deve ter:

- loading
- empty state
- error state
- success feedback quando fizer ação