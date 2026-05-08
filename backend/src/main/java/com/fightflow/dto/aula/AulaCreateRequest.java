package com.fightflow.dto.aula;

import com.fightflow.entity.AulaTipo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record AulaCreateRequest(
    @NotBlank @Size(max = 120) String titulo,
    @Size(max = 1000) String descricao,
    @NotNull AulaTipo tipo,
    @NotNull Instant dataHoraInicio,
    @NotNull Instant dataHoraFim,
    @Min(1) Integer capacidade
) {}

