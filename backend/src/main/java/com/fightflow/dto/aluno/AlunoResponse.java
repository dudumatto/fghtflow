package com.fightflow.dto.aluno;

import com.fightflow.entity.Role;
import java.time.Instant;

public record AlunoResponse(
    Long id,
    Long usuarioId,
    String nome,
    String email,
    Role role,
    Long academiaId,
    String academiaNome,
    boolean ativo,
    String faixaAtual,
    int grauAtual,
    Instant createdAt
) {}
