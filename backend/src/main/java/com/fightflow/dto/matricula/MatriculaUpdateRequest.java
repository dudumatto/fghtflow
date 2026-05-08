package com.fightflow.dto.matricula;

import com.fightflow.entity.MatriculaStatus;
import java.time.Instant;

public record MatriculaUpdateRequest(
    Long planoId,
    Instant dataInicio,
    Instant dataFim,
    MatriculaStatus status
) {}

