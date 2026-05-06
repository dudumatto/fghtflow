# Skill: UI Accessibility (A11y)

## Objetivo
Garantir acessibilidade prática com mudanças pequenas e consistentes (sem “overengineering”).

## Práticas (curtas)
- Semântica primeiro: usar elementos corretos (`button`, `a`, `label`, `fieldset`, `dialog`) antes de ARIA.
- Foco visível e lógico: outline/box-shadow claro; ordem de tab previsível.
- Inputs: `label` associado; mensagens de erro ligadas ao campo (`aria-describedby`) quando aplicável.
- Ações: área de toque ≥ `44px` no mobile.
- Contraste: evitar texto “cinza fraco” em fundos claros; atenção em estados disabled/muted.
- Motion: respeitar `prefers-reduced-motion` e evitar animações que atrapalhem leitura.

## Checklist rápido
- Dá para navegar tudo só com teclado?
- O foco aparece sempre e não “se perde” em modais/menus?
- Erros são claros (o que aconteceu e como resolver)?
- Ícones clicáveis têm rótulo acessível?

