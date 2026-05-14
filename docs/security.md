# Seguranca

## Autenticacao

- Access token JWT enviado pelo frontend nas chamadas autenticadas.
- Refresh token via cookie HttpOnly `ff_refresh`.
- Fluxos principais:
  - `POST /auth/register`
  - `POST /auth/login`
  - `POST /auth/refresh`
  - `POST /auth/logout`

## Autorizacao

- RBAC no backend com validacoes no Spring Security e nos services.
- Anti-IDOR aplicado no service: ids recebidos do cliente devem ser validados contra ownership/escopo.
- Professores sao escopados por academia; administradores possuem visao global.

## Upload seguro

- Armazenamento por usuario em `uploads/{usuarioId}/`.
- Nome de arquivo interno por UUID.
- Limite de 10MB.
- Allowlist de PDF/DOC/DOCX por MIME e extensao.
- Preview apenas para PDF.

## Pontos sensiveis

- CORS com credenciais deve ser restrito em producao.
- `JWT_SECRET` deve ser forte e definido por variavel de ambiente em producao.
- Evitar lazy loading fora de transacao em endpoints autenticados.
