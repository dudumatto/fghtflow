# WAVE 4 — Mensalidades

Objetivo:
- somente PROFESSOR/Admin gerencia mensalidades
- aluno/atleta apenas visualiza/paga

Implementar:
- histórico de mensalidades
- status:
  - PENDENTE
  - PAGA
  - VENCIDA
  - CANCELADA

Endpoints:
- GET /mensalidades/minhas
- GET /mensalidades/pendentes
- PATCH /mensalidades/{id}/pagar
- PATCH /mensalidades/{id}/marcar-paga

Frontend:
- dashboard financeiro
- histórico
- botão pagar
- status visual

Regras:
- PROFESSOR apenas academias dele
- aluno vê apenas próprias mensalidades

Executar:
- mvn test
- npm build
- e2e financeiro

Salvar memória.