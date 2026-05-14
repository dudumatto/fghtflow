# FightFlow — RBAC

## Roles

- ADMIN
- PROFESSOR
- ATLETA
- ALUNO

## ADMIN

Pode:

- gerenciar academias
- gerenciar professores
- gerenciar alunos
- gerenciar atletas
- gerenciar mensalidades
- gerenciar presença
- gerenciar graduação
- visualizar dashboards globais
- acessar todas as academias

## PROFESSOR

Pode:

- criar academia
- gerenciar academias vinculadas
- cadastrar alunos nas academias dele
- cadastrar atletas nas academias dele
- gerenciar mensalidades das academias dele
- registrar presença
- registrar graduação
- gerenciar aulas
- confirmar aulas privadas
- gerenciar aulas experimentais
- visualizar dashboard da academia

Não pode:

- acessar academia de outro professor
- gerenciar aluno de academia que não controla
- gerenciar mensalidade fora do escopo
- acessar dados globais de admin

## ATLETA

Pode:

- visualizar próprio dashboard
- visualizar própria mensalidade
- pagar mensalidade
- visualizar própria graduação
- visualizar evolução
- visualizar/registrar área esportiva quando permitido
- acessar lutas
- acessar competições
- acessar treinos
- visualizar perfil

Não pode:

- gerenciar mensalidades
- gerenciar presença
- gerenciar graduação
- cadastrar alunos
- cadastrar atletas
- gerenciar academias
- acessar dados de outro atleta

## ALUNO

Pode:

- visualizar próprio dashboard
- visualizar mensalidades próprias
- pagar mensalidade
- visualizar graduação própria
- visualizar agenda
- visualizar perfil

Não pode:

- gerenciar competições
- gerenciar lutas
- gerenciar treinos esportivos avançados
- gerenciar mensalidade
- gerenciar presença
- gerenciar graduação
- cadastrar usuários
- gerenciar academia

## Matriz de permissões

| Feature | ADMIN | PROFESSOR | ATLETA | ALUNO |
|---|---:|---:|---:|---:|
| Academias | Sim | Escopo próprio | Não | Não |
| Alunos | Sim | Escopo próprio | Não | Não |
| Atletas | Sim | Escopo próprio | Próprio | Não |
| Mensalidades gestão | Sim | Escopo próprio | Não | Não |
| Mensalidades visualizar | Sim | Escopo próprio | Próprias | Próprias |
| Presença registrar | Sim | Escopo próprio | Não | Não |
| Presença visualizar | Sim | Escopo próprio | Própria | Própria |
| Graduação registrar | Sim | Escopo próprio | Não | Não |
| Graduação visualizar | Sim | Escopo próprio | Própria | Própria |
| Lutas | Sim | Escopo próprio | Sim | Não |
| Competições | Sim | Escopo próprio | Sim | Não |
| Treinos | Sim | Escopo próprio | Sim | Limitado/Não |
| Dashboard | Global | Academia | Próprio | Próprio |
| Aula privada | Sim | Gerencia | Solicita | Solicita se permitido |
| Aula experimental | Sim | Gerencia | Visualiza/Solicita | Visualiza/Solicita |