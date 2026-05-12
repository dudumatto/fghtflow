package com.fightflow.dto.atleta;

import com.fightflow.entity.Role;
import java.time.Instant;

public record AtletaResponse(
    Long id,
    Long usuarioId,
    Long alunoId,
    String nome,
    String email,
    Role role,
    Long academiaId,
    String academiaNome,
    boolean ativo,
    String faixa,
    int grauAtual,
    Double peso,
    String categoria,
    Instant createdAt
) {}
