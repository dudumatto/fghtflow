package com.fightflow.dto.atleta;

import jakarta.validation.constraints.Size;

public record AtletaUpdateRequest(
    @Size(max = 30) String faixa,
    Double peso,
    @Size(max = 60) String categoria
) {}

