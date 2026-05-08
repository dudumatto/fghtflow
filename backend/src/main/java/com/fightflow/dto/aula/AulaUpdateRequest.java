package com.fightflow.dto.aula;

import com.fightflow.entity.AulaTipo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record AulaUpdateRequest(
    @Size(max = 120) String titulo,
    @Size(max = 1000) String descricao,
    AulaTipo tipo,
    Instant dataHoraInicio,
    Instant dataHoraFim,
    @Min(1) Integer capacidade,
    Boolean ativa
) {}

