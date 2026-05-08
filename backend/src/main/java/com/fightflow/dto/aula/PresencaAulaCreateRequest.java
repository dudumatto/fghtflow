package com.fightflow.dto.aula;

import com.fightflow.entity.PresencaAulaStatus;
import jakarta.validation.constraints.NotNull;

public record PresencaAulaCreateRequest(
    @NotNull Long alunoId,
    PresencaAulaStatus status
) {}

