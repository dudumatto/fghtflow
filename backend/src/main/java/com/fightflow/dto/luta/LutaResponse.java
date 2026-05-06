package com.fightflow.dto.luta;

import com.fightflow.entity.LutaMetodo;
import com.fightflow.entity.LutaResultado;
import java.time.Instant;

public record LutaResponse(
    Long id,
    Long atletaId,
    Long competicaoId,
    String adversarioNome,
    LutaResultado resultado,
    LutaMetodo metodo,
    Instant foughtAt
) {}

