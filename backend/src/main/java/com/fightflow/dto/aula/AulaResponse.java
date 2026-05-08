package com.fightflow.dto.aula;

import com.fightflow.entity.AulaTipo;
import java.time.Instant;

public record AulaResponse(
    Long id,
    Long professorUsuarioId,
    String titulo,
    String descricao,
    AulaTipo tipo,
    Instant dataHoraInicio,
    Instant dataHoraFim,
    Integer capacidade,
    boolean ativa
) {}

