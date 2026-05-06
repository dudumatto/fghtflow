package com.fightflow.dto.presenca;

import jakarta.validation.constraints.NotNull;

public record PresencaCreateRequest(
    @NotNull Long treinoId,
    @NotNull Long atletaId
) {}

