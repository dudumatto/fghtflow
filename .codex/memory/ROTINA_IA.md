---

# ROTINA_IA.md

# 🤖 Rotina Operacional da IA

## 📌 Objetivo

Definir como os agentes devem:

* iniciar tarefas
* consumir memória
* alterar código
* registrar conhecimento
* finalizar tarefas

---

# 🟢 Ao iniciar

## Sempre:

1. Ler AGENTS.md
2. Ler apenas contexto necessário
3. Ler memória relacionada à tarefa atual
4. Consultar PROJECT_CONTEXT.md
5. Consultar README.md
6. Consultar MEMORY.md quando necessário

---

# 🟡 Durante a tarefa

## Sempre:

* consultar arquivos reais antes de alterar
* evitar assumir comportamento do sistema
* evitar contexto desnecessário
* evitar repetir análises já salvas
* fazer mudanças pequenas e seguras
* manter compatibilidade
* reutilizar código existente
* reutilizar componentes existentes
* priorizar token efficiency
* evitar arquivos desnecessários

---

# 🔴 Ao finalizar

## Sempre gerar:

* resumo curto
* problema encontrado
* solução aplicada
* arquivos alterados
* próximos passos
* aprendizado reutilizável

---

# 💾 Salvar memória

## Node CLI

```bash
node scripts/memory/memory-cli.mjs save --project tcc --text "Resumo da alteração"
```

## PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File E:/second-brain/scripts/save-memory.ps1 -Project tcc -Text "Resumo da alteração"
```

---

# 🧠 Regras de Memória

## Salvar apenas:

* bugs importantes
* soluções reutilizáveis
* decisões arquiteturais
* padrões úteis
* workflows úteis
* problemas recorrentes
* soluções de integração

---

# 🚫 Nunca salvar

* conversa inteira
* logs gigantes
* contexto temporário
* código irrelevante
* pensamento redundante
* arquivos enormes

---

# ⚡ Regras de Eficiência

## Sempre:

* carregar contexto progressivamente
* usar modularização
* usar referências ao invés de duplicação
* evitar leitura desnecessária
* minimizar consumo de token
* usar skills sob demanda
* usar agentes especializados

---

# 🧩 Multi-Agentes

## Objetivo

Permitir:

* agentes paralelos
* múltiplos Codex simultâneos
* workflows especializados
* separação de responsabilidades

---

# 📂 Estrutura Recomendada

## Global (.codex)

```txt
.codex/
  agents/
  skills/
  rules/
  workflows/
```

Responsável por:

* comportamento global
* regras globais
* skills reutilizáveis
* workflows reutilizáveis

---

## Projeto

```txt
README.md
PROJECT_CONTEXT.md
MEMORY.md
TASKS.md
```

Responsável por:

* contexto específico
* arquitetura específica
* decisões técnicas
* memória do projeto

---

# 🎯 Resultado Esperado

* menor consumo de token
* maior autonomia
* melhor organização
* melhor paralelismo
* melhor escalabilidade
* contexto limpo
* reutilização máxima
* melhor performance para Codex/GPT/Claude/Gemini
