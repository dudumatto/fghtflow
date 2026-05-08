package com.fightflow.dto.matricula;

import com.fightflow.entity.MatriculaStatus;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record MatriculaCreateRequest(
    @NotNull Long alunoId,
    @NotNull Long planoId,
    @NotNull Instant dataInicio,
    Instant dataFim,
    MatriculaStatus status
) {}

