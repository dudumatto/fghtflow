# FightFlow — API Architecture

## Convenção

Endpoints atuais são SEM `/api`.

Não criar `/api` sem decisão explícita.

## Envelope

Respostas devem usar ApiResponse.

Formato esperado:

```json
{
  "success": true,
  "data": {},
  "error": null
}