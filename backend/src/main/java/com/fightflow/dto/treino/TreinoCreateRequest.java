package com.fightflow.dto.treino;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record TreinoCreateRequest(
    @NotNull Instant startsAt,
    @NotBlank @Size(max = 120) String titulo,
    @Size(max = 1000) String descricao
) {}

