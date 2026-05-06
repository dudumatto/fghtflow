# AGENTS.md (Agent Kit)

## Objetivo

Organizar o trabalho em **especialistas** (agentes) com checklists curtos e saídas consistentes.

## Agentes disponíveis

- **Design/UI Agent** → `agents/DESIGN_UI.agent.md`
- **Frontend Agent** → `agents/FRONTEND.agent.md`
- **Backend Agent** → `agents/BACKEND.agent.md`
- **QA/Test Agent** → `agents/QA_TEST.agent.md`
- **Security Agent** → `agents/SECURITY.agent.md`
- **Documentation Agent** → `agents/DOCUMENTATION.agent.md`

## Regras globais (sempre)

- Ler arquivos antes de sugerir mudanças (sem suposições).
- Preferir mudanças pequenas e compatíveis.
- Se houver dúvida, declarar explicitamente.
- Segurança e compatibilidade têm prioridade sobre estética/performance.
- Se houver impacto visual/UX, o **Design/UI Agent** revisa antes de concluir.

## Fluxo padrão

Regras detalhadas: `rules/COLLABORATION.md`.

1. Selecionar agentes mínimos necessários.
2. Cada agente faz uma análise curta.
3. Consolidar decisões e riscos.
4. Implementar.
5. Validar (testes/build + checklist QA/Design).
