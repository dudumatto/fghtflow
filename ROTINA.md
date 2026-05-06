
# Mini Rotina de Memória da IA

## Ao iniciar
1. Ler o arquivo principal de memória do vault.
2. Ler apenas as memórias relacionadas à tarefa atual.
3. Usar essas informações como contexto antes de responder ou alterar código.

node scripts/load-memory.js

## Durante a tarefa
1. Consultar arquivos reais do projeto antes de decidir.
2. Evitar repetir análises já salvas.
3. Fazer mudanças pequenas, seguras e compatíveis.

## Ao finalizar
1. Gerar um resumo curto do que foi feito.
2. Registrar:
   - problema encontrado
   - solução aplicada
   - arquivos importantes
   - próximos passos
3. Salvar ou atualizar uma memória no vault.

node scripts/save-memory.js "titulo" "conteudo"
node scripts/learn-tcc.js





## Regra
Salvar apenas conhecimento útil, curto e reutilizável.
Não salvar conversa inteira.


# 🧠 Sistema de Memória com IA (Obsidian + Agente)

## 📁 Estrutura

Vault:
E:/second-brain

Pastas:

* 01-knowledge/
* AGENTS.md

Projeto TCC:

* scripts/load-memory.js
* scripts/save-memory.js

---

## ⚙️ Scripts

### ▶️ load-memory.js

Carrega:

* AGENTS.md
* últimas memórias

Uso:
node scripts/load-memory.js

---

### 💾 save-memory.js

Salva nova memória no Obsidian

Uso:
node scripts/save-memory.js "titulo" "conteudo"

---

## 🔁 Fluxo diário

### 🟢 INÍCIO

1. Abrir terminal no projeto TCC
2. Rodar:

node scripts/load-memory.js

3. Copiar saída
4. Colar no agente:

"Use isso como memória do projeto:"

---

### 🟡 DURANTE

* Trabalhar normalmente com a IA
* Sempre usar:
  "baseado na minha memória..."

---

### 🔴 FINAL

1. Pedir para IA:

"Resuma o que foi feito e gere comando de memória"

2. Rodar comando gerado:

node scripts/save-memory.js "titulo" "conteudo"

---

## 🧠 Regra no AGENTS.md

Sempre:

* Aprender com bugs e soluções
* Gerar memória em markdown
* Gerar comando save-memory

---

## ⚠️ Boas práticas

* Salvar apenas conhecimento útil
* Evitar duplicação
* Manter memórias curtas

---

## ✅ Resultado

* IA aprende com você
* Memória persistente no Obsidian
* Menos uso de tokens
* Evolução contínua
