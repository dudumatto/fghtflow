# Skill: Motion & Feedback

## Objetivo
Usar animações/transições apenas para dar feedback e melhorar percepção de qualidade.

## Práticas (curtas)
- Transições curtas: `150–250ms`, easing consistente (evitar “elástico” sem propósito).
- Animação como feedback: hover/focus/pressed, loaders discretos, entrada/saída de modais.
- Evitar animação de layout grande (reflow): preferir opacity/transform.
- Redução de movimento: desativar/encurtar com `prefers-reduced-motion`.

## Checklist rápido
- A animação ajuda a entender o que mudou?
- Não atrapalha leitura nem causa enjoo?
- Há fallback claro quando motion é reduzido?

