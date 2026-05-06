package com.fightflow.dto.luta;

import com.fightflow.entity.LutaMetodo;
import com.fightflow.entity.LutaResultado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record LutaCreateRequest(
    @NotNull Long atletaId,
    Long competicaoId,
    @Size(max = 140) String adversarioNome,
    @NotNull LutaResultado resultado,
    @NotNull LutaMetodo metodo,
    @NotNull Instant foughtAt
) {}

