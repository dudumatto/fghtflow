# Arquitetura

## Visao geral

FightFlow e um sistema fullstack com API Spring Boot e frontend React/Vite.

## Backend

Pacote base: `backend/src/main/java/com/fightflow`

- `controller/`: endpoints REST finos.
- `service/`: regras de negocio e autorizacao/ownership.
- `repository/`: JPA repositories e Specifications.
- `security/`: JWT, filtros stateless e handlers REST.
- `exception/`: mapeamento global de erros e envelope.
- `dto/`: contratos de request/response.
- `entity/`: entidades JPA.
- `util/`: helpers compartilhados.

Padrao de camadas:

```txt
controller -> service -> repository -> dto
```

## Frontend

- `frontend/src/services/`: client HTTP, envelope e refresh token.
- `frontend/src/state/`: estado de autenticacao.
- `frontend/src/pages/`: telas principais.
- `frontend/src/components/`: componentes reutilizaveis.
- `frontend/src/layout/`: shell, menu e rotas protegidas.
- `frontend/src/permissions.ts`: permissoes de menu/rotas no frontend.

## Integracao

- Backend e frontend usam endpoints sem prefixo `/api`.
- O frontend le `VITE_API_BASE` para encontrar a API.
- A API responde com envelope padronizado `{ success, data, error }`.
