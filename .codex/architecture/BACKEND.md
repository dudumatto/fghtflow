# FightFlow — Backend Architecture

## Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- JWT + Refresh Token

## Padrão obrigatório

Usar sempre:

controller -> service -> repository -> dto

Não colocar regra de negócio no controller.

## Estrutura esperada

- controller: recebe request, valida role básica, chama service
- service: regra de negócio, anti-IDOR, validação de academia/permissão
- repository: acesso ao banco
- dto: entrada e saída da API
- entity: modelo persistente
- exception: erros tratados
- security: JWT, filtros e autenticação

## Convenções

- Endpoints SEM `/api`
- Usar envelope `ApiResponse`
- Não expor senha/hash
- Não retornar entity diretamente
- Retornar DTO
- Validações de permissão no backend são obrigatórias
- Frontend esconder botão/menu não substitui segurança

## Fluxo de autenticação

- Login gera access token
- Refresh token mantém sessão
- UserPrincipal carrega:
  - usuarioId
  - role
  - academiaId

## Compatibilidade importante

Não remover `Usuario.academia`.

Motivo:
- usado no JWT atual
- usado no escopo antigo
- usado como fallback de compatibilidade

Para professor com múltiplas academias, usar:

- ProfessorAcademia
- AcademiaScopeService

## Serviços importantes

### AcademiaScopeService

Responsável por validar:

- professor gerencia academia
- admin possui acesso global
- aluno/atleta acessa apenas própria academia
- fallback por Usuario.academia

Deve ser usado em qualquer feature com academia.

## Regras de implementação

Antes de criar nova feature:

1. verificar entity existente
2. verificar DTO existente
3. reaproveitar service se possível
4. manter endpoint sem `/api`
5. adicionar testes
6. validar anti-IDOR

## Erros

Não deixar NullPointerException virar 500.

Usar:

- 400 para request inválido
- 401 para não autenticado
- 403 para sem permissão
- 404 para recurso inexistente
- 409 para conflito/regra de negócio
- 500 apenas para erro interno real