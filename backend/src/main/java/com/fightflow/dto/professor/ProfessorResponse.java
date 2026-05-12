package com.fightflow.dto.professor;

import com.fightflow.entity.Role;
import java.time.Instant;

public record ProfessorResponse(
    Long usuarioId,
    String nome,
    String email,
    Role role,
    Long academiaId,
    String academiaNome,
    Instant createdAt
) {}
