package com.fightflow.dto.aula;

import com.fightflow.entity.PresencaAulaStatus;
import java.time.Instant;

public record PresencaAulaResponse(
    Long id,
    Long alunoId,
    PresencaAulaStatus status,
    Instant registradaEm
) {}

