package com.fightflow.dto.aula;

import com.fightflow.entity.PresencaAulaStatus;
import jakarta.validation.constraints.NotNull;

public record PresencaAulaUpdateRequest(
    @NotNull Long alunoId,
    @NotNull PresencaAulaStatus status
) {}

