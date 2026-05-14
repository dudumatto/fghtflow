# FightFlow — Security Architecture

## Princípios

- Backend é a fonte final de segurança.
- Frontend melhora UX, mas não protege dados sozinho.
- Toda rota sensível precisa validar role e propriedade do recurso.
- Anti-IDOR obrigatório.

## IDOR

IDOR acontece quando um usuário acessa recurso de outro usuário apenas trocando ID.

Exemplo proibido:

- aluno acessar `/mensalidades/999` de outro aluno
- professor acessar aluno de academia que não gerencia
- atleta alterar graduação
- aluno acessar competição administrativa

## Regra base

Antes de retornar ou alterar qualquer recurso:

1. identificar usuário logado
2. identificar role
3. identificar academia relacionada
4. validar se o usuário pode acessar
5. se não puder, retornar 403 ou 404

## Roles

- ADMIN: acesso global
- PROFESSOR: acesso às academias vinculadas
- ATLETA: acesso próprio e área esportiva permitida
- ALUNO: acesso próprio e leitura limitada

## JWT

UserPrincipal deve conter:

- usuarioId
- role
- academiaId

Não confiar em dados enviados no request para identificar dono.

## Senhas

- nunca retornar senha/hash
- nunca logar senha
- nunca versionar `.env`

## Uploads

Regras já esperadas:

- limite de tamanho
- allowlist de MIME/extensão
- nome original sanitizado
- arquivo salvo com UUID
- bloquear path traversal
- preview apenas PDF quando aplicável

## Erros

Não vazar stack trace para o usuário.

Mapear:

- 401 não autenticado
- 403 sem permissão
- 404 não encontrado
- 409 conflito
- 413 arquivo grande
- 415 tipo inválido

## Logs

Logs devem ajudar debugging, mas não expor:

- senha
- token
- refresh token
- dados sensíveis

## Checklist de nova feature

- Tem role?
- Tem academia?
- Tem dono?
- Tem anti-IDOR?
- Tem teste de acesso proibido?
- Frontend esconde menu?
- Backend bloqueia mesmo se chamar manualmente?