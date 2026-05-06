# Skill: Design System

## Objetivo
Manter consistência visual com tokens, componentes e regras simples.

## Práticas (curtas)
- Preferir **tokens** (cores, spacing, radius, shadow, typography) em vez de valores “soltos”.
- Definir escala: spacing `4/8/12/16/24/32/48/64`.
- Tipografia: poucos níveis (ex: `title`, `subtitle`, `body`, `caption`) e pesos limitados; definir `line-height` e limites de largura de texto quando possível.
- Cores: definir semântica (ex: `--fg`, `--bg`, `--muted`, `--primary`, `--danger`, `--border`) antes de escolher “tons bonitos”.
- Componentes com variantes: `size`, `variant`, `state` (hover/active/disabled).
- Motion: transições `150–250ms` e respeitar `prefers-reduced-motion`.
- Layout: usar primitives (Container/Stack/Grid) ou padrões equivalentes para evitar “margens aleatórias”.

## Checklist rápido
- Existe uma fonte única de tokens?
- Componentes reutilizam tokens?
- Estados (hover/active/disabled/focus) estão consistentes?
- Há padrão de responsividade (mobile-first) repetível?
