package com.fightflow.dto.matricula;

import com.fightflow.entity.MatriculaStatus;
import java.time.Instant;

public record MatriculaResponse(
    Long id,
    Long alunoId,
    Long planoId,
    Instant dataInicio,
    Instant dataFim,
    MatriculaStatus status,
    Instant createdAt
) {}

