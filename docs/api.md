# API

## Convencoes

- Endpoints atuais nao usam prefixo `/api`.
- Respostas seguem o envelope `ApiResponse`.

## Envelope

Sucesso:

```json
{ "success": true, "data": {} }
```

Erro:

```json
{ "success": false, "error": "Forbidden" }
```

## Autenticacao

Login e cadastro retornam access token no corpo da resposta e definem refresh cookie:

```http
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout
```

## Paginacao e filtros

Endpoints de listagem aceitam `page`, `size`, `sort` e filtros especificos.

```http
GET /lutas?page=0&size=20&sort=foughtAt,desc&atletaId=1&resultado=WIN&dateFrom=2026-01-01T00:00:00Z&dateTo=2026-12-31T23:59:59Z
GET /competicoes?page=0&size=20&sort=startsAt,desc&dateFrom=2026-01-01T00:00:00Z
GET /treinos?page=0&size=20&sort=startsAt,desc
```
