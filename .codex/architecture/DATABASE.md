# FightFlow — Database Architecture

## Banco

- PostgreSQL
- JPA/Hibernate
- ddl-auto update em desenvolvimento

## Atenção

Não há migrations versionadas robustas ainda.

Toda mudança de entidade deve considerar:

- dados existentes
- compatibilidade
- nullable
- valores default
- enums existentes

## Entidades principais

### Usuario

Representa login e identidade base.

Campos importantes:

- id
- nome
- email
- senhaHash
- role
- academia

Compatibilidade:
- não remover `Usuario.academia`

## Academia

Representa academia/unidade.

Campos esperados:

- id
- nome
- endereco
- ativo
- professorResponsavel
- createdAt
- updatedAt

## ProfessorAcademia

Relaciona professor com múltiplas academias.

Campos:

- id
- professor
- academia
- papel
- ativo
- createdAt
- updatedAt

Papéis:

- RESPONSAVEL
- INSTRUTOR

## Aluno

Representa aluno administrativo/financeiro.

Relações:

- usuario
- academia
- mensalidades
- presenças
- graduações

## Atleta

Representa perfil esportivo.

Relações:

- usuario
- academia
- lutas
- competições
- evolução
- graduação

## Mensalidade

Representa cobrança.

Campos esperados:

- id
- aluno/atleta
- academia
- professorResponsavel
- valor
- vencimento
- status
- dataPagamento
- formaPagamento
- cobrancaAutomatica
- createdAt
- updatedAt

Status desejado:

- PENDENTE
- PAGA
- VENCIDA
- CANCELADA

## Aula

Representa aula coletiva ou agenda.

## PresencaAula

Representa presença em aula.

## Graduacao

Representa histórico de faixas/graus.

Campos esperados:

- aluno/atleta
- faixa
- grau
- dataGraduacao
- professorResponsavel
- observacao

## Regras de relacionamento

- Professor pode ter várias academias via ProfessorAcademia
- Aluno pertence a uma academia
- Atleta pertence a uma academia
- Mensalidade pertence ao aluno/atleta e academia
- Presença pertence ao aluno/atleta e aula
- Graduação pertence ao aluno/atleta

## Cuidados

- Evitar cascade perigoso
- Soft delete onde fizer sentido
- Não deletar dados financeiros reais
- Não deletar histórico de graduação/presença