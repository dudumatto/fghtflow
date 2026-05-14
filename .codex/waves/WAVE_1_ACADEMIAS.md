# WAVE 1 — Academias + escopo seguro

Objetivo:
- CRUD de academias
- professor relacionado a múltiplas academias
- anti-IDOR
- select de academias
- frontend mínimo

Regras:
- não remover Usuario.academia
- manter compatibilidade
- endpoints sem /api
- ADMIN global
- PROFESSOR apenas academias dele

Implementar:
- AcademiaController
- AcademiaService
- ProfessorAcademia
- AcademiaScopeService
- GET /academias/select
- tela frontend de academias

Executar:
- mvn test
- npm run build
- playwright se existir

Salvar memória do projeto.