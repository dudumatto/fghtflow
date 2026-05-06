# Agent Kit (reutilizável)

Estrutura base de **agentes**, **skills** e **rules** para copiar e reutilizar em qualquer projeto.

## Objetivo

Simular um fluxo “paralelo” (estilo *Claude Design + especialistas*) mesmo quando só existe um único assistente:

1. Identificar agentes relevantes
2. Cada agente analisa sua área (checklists curtos e objetivos)
3. Consolidar decisões e conflitos
4. Aplicar mudanças (mínimas e seguras)
5. Validar (build/test/checklist)

Regra-chave: se houver impacto em **UI/UX/visual/layout/responsividade/a11y**, o **Design/UI Agent** revisa decisões visuais antes da conclusão.

## Estrutura

- `AGENTS.md` — índice de agentes e quando usar cada um
- `RULES.md` — regras globais e fluxo de colaboração
- `SKILLS.md` — índice de skills (curtas e consultáveis)
- `agents/` — definições dos agentes (prompts/rotinas)
- `rules/` — regras detalhadas (workflow, formatos)
- `skills/` — skills reutilizáveis por tema

## Como “chamar” os agentes (na prática)

Você pode pedir explicitamente:

- “Use o **Design/UI Agent** e depois aplique as mudanças no frontend”
- “Rode o fluxo completo (Design + Frontend + Backend + Security + QA)”
- “Só análise (sem mudar código) usando Design/UI + QA”

Se você não especificar, o **orquestrador** (regras em `rules/COLLABORATION.md`) escolhe os agentes mínimos necessários para a tarefa.

## Templates rápidos de pedido (copiar/colar)

- Análise UI/UX (sem código): “Analise esta tela com o Design/UI Agent e me devolva o checklist + prioridades (sem implementar).”
- Correção UI/UX (com código): “Use Design/UI + Frontend e aplique as mudanças mínimas para melhorar responsividade e acessibilidade.”
- Mudança de API com segurança: “Use Backend + Security + QA. Não quebre contratos; proponha migração se precisar.”
- Entrega completa: “Fluxo completo (Design/UI, Frontend, Backend, Security, QA, Docs) e valide com comandos/checklist.”

## Como combinar agents + skills + rules

- **Agents** = *quem* analisa (responsabilidade e checklist).
- **Skills** = *como* executar bem (práticas e padrões).
- **Rules** = *quando* envolver cada agente, como consolidar e como entregar.

Sugestão:

1. Comece pelo(s) agente(s) principal(is) do domínio (ex: Design/UI + Frontend).
2. Acrescente **Security** e **QA/Test** em qualquer tarefa com risco/produção.
3. Use as skills correspondentes na execução (ex: `skills/design-system.md`).

## Como copiar para outro projeto

1. Copie a pasta inteira para a raiz do projeto (ou para `.ai/agent-kit/`).
2. Ajuste apenas detalhes do projeto no topo dos arquivos (stack, padrões locais).
3. Evite acoplar a referências externas (links de vault, caminhos locais, etc.).

## Arquivos-chave

- Seleção e coordenação: `rules/COLLABORATION.md`
- Formato de entrega (tarefas complexas): `rules/OUTPUT_FORMAT.md`
- Definições dos agentes: `agents/`
- Skills práticas: `skills/`
