# Regras de colaboração (fluxo paralelo simulado)

## Objetivo

Simular “agentes em paralelo” com um único assistente, sem perder qualidade:

1) selecionar agentes → 2) análises curtas → 3) consolidar → 4) implementar → 5) validar.

## Seleção de agentes (mínimo necessário)

Use esta tabela como gatilho:

- UI/CSS/tema/layout → **Design/UI + Frontend**
- Contrato/API/regra de negócio → **Backend + Frontend**
- Auth/permissões/uploads/dados sensíveis → **Security + Backend + QA**
- Bug sem causa clara → **QA + (Backend/Frontend) + Security se envolver inputs**
- Feature nova (fluxo) → **Design/UI + Frontend + Backend + QA + Docs**

## Ordem de execução (sempre)

1. **Entender e ler** os arquivos relevantes (sem suposições).
2. Rodar análise dos agentes selecionados, cada um com 3–7 bullets.
3. Consolidar: listar decisões, trade-offs e riscos.
4. Implementar mudanças (mínimas, seguras, compatíveis).
5. Validar: testes/linters/build + checklist manual do QA/Design.

## Regra “Design antes de concluir”

Se houver qualquer impacto em **UI/UX/visual/layout/responsividade/acessibilidade**, o **Design/UI Agent** deve:

- revisar decisões visuais (tokens, spacing, tipografia, hierarquia, estados)
- apontar mudanças mínimas e riscos de regressão visual
- deixar um checklist de validação (incluindo mobile/teclado)

Backend/Security/QA/Docs só acionam design quando houver impacto visual/experiência do usuário.

## Como simular “paralelo” (sem ferramentas)

Quando a tarefa envolver múltiplas áreas:

1. Escolher o agente “dono” (ex: Frontend ou Backend).
2. Rodar análises curtas dos demais **antes** de codar (evita retrabalho).
3. Se o trabalho for visual, faça um micro-ciclo:
   - Design/UI define decisões e checklist → Frontend implementa → Design/UI valida → QA valida.

## Resolução de conflitos

Se agentes “discordarem”:

- Priorizar **segurança** > **correção** > **compatibilidade** > **UX** > **performance** > **estética**.
- Registrar a decisão (curta) e a alternativa descartada.
