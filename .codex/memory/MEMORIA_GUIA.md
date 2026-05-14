# MEMORIA_GUIA.md

# 🧠 Sistema de Memória com IA

## 📌 Objetivo

Sistema de memória integrado ao Obsidian para:

* reduzir uso de tokens
* manter contexto persistente
* melhorar aprendizado contínuo
* reutilizar conhecimento entre projetos
* melhorar performance de agentes
* centralizar memória técnica

---

# 📁 Estrutura Recomendada

```txt
.codex/
  agents/
  skills/
  rules/
  workflows/
  templates/

02-projects/
  projeto/
    README.md
    PROJECT_CONTEXT.md
    MEMORY.md
    LEARNINGS.md
    TASKS.md
```

---

# 🚀 Uso Diário

## 1. Carregar contexto do projeto

### Node CLI

```bash
node scripts/memory/memory-cli.mjs load --project tcc
```

### PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File E:/second-brain/scripts/load-context.ps1 -ProjectName tcc
```

---

## 2. Salvar memória importante

### Node CLI

```bash
node scripts/memory/memory-cli.mjs save --project tcc --text "Descrição do que foi feito"
```

### PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File E:/second-brain/scripts/save-memory.ps1 -Project tcc -Text "Descrição do que foi feito"
```

### Via pipe

```powershell
"texto" | powershell -ExecutionPolicy Bypass -File E:/second-brain/scripts/save-memory.ps1 -Project tcc -Stdin
```

---

## 3. Gerar aprendizado

### Node CLI

```bash
node scripts/memory/memory-cli.mjs learn --project tcc
```

### PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File E:/second-brain/scripts/learn-project.ps1 -Project tcc
```

---

## 4. Gerar conexões semânticas (Graphify)

### Projeto

```bash
node scripts/memory/memory-cli.mjs graphify project --project tcc
```

### Vault inteiro

```bash
node scripts/memory/memory-cli.mjs graphify vault
```

---

# 📅 Uso Semanal

## Aprender com TODO o vault

```bash
node scripts/memory/memory-cli.mjs learn vault
```

ou

```powershell
powershell -ExecutionPolicy Bypass -File E:/second-brain/scripts/learn-vault.ps1
```

---

# 🏗️ Criar Novo Projeto

## Estrutura obrigatória

Criar em:

```txt
02-projects/{nome-do-projeto}/
```

Arquivos:

```txt
README.md
PROJECT_CONTEXT.md
MEMORY.md
LEARNINGS.md
TASKS.md
```

---

# 📄 Templates

## README.md

```md
# Nome do Projeto

## Contexto
-

## Tecnologias
-

## Status
- inicial

## 🔗 Relacionamentos
-
```

---

## PROJECT_CONTEXT.md

```md
# Contexto do Projeto

## Objetivo
-

## Arquitetura
-

## Decisões
-

## Próximos passos
-

## 🔗 Relacionamentos
-
```

---

## MEMORY.md

```md
# Memória do Projeto

## Log
-

## 🔗 Relacionamentos
-
```

---

## LEARNINGS.md

```md
# Aprendizados

-

## 🔗 Relacionamentos
-
```

---

## TASKS.md

```md
# Tarefas

- [ ]

## 🔗 Relacionamentos
-
```

---

# 🔁 Integração Inicial

```bash
node scripts/memory/memory-cli.mjs learn --project {nome-do-projeto}
```

```bash
node scripts/memory/memory-cli.mjs graphify project --project {nome-do-projeto}
```

---

# ⚡ Boas Práticas

* salvar apenas conhecimento útil
* evitar duplicação
* manter memórias curtas
* modularizar contexto
* evitar arquivos gigantes
* usar links Obsidian
* priorizar token efficiency
* reutilizar contexto existente
* separar contexto global de contexto do projeto

---

# 🎯 Resultado Esperado

* IA aprende continuamente
* menos uso de tokens
* memória persistente
* melhor performance de agentes
* melhor reutilização
* contexto mais limpo
* melhor organização
* escalabilidade para múltiplos projetos

---
