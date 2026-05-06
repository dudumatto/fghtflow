# RULES.md (Agent Kit)

## Objetivo

Garantir execução **segura**, **consistente** e **reutilizável** em qualquer projeto.

## Regras globais (obrigatórias)

- Ler arquivos antes de sugerir mudanças (sem suposições).
- Mudanças mínimas: corrigir a causa raiz com o menor impacto possível.
- Não quebrar contratos/integrações sem migração explícita.
- Não criar arquivos novos sem justificativa.
- Se estiver incerto, dizer explicitamente e pedir contexto.

## Colaboração entre agentes

Workflow oficial: `rules/COLLABORATION.md`.

- Tarefas simples: 1 agente principal + (QA ou Security se houver risco).
- Tarefas complexas: selecionar múltiplos agentes e consolidar decisões.

## Prioridades para decisões

1. Segurança
2. Correção funcional
3. Compatibilidade/contratos
4. UX (Design/UI) — quando houver impacto visual, revisar antes de concluir
5. Performance
6. Organização/refino

## Formato de entrega

Para tarefas complexas, usar: `rules/OUTPUT_FORMAT.md`.
