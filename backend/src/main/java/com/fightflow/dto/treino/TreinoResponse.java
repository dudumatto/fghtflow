package com.fightflow.dto.treino;

import java.time.Instant;

public record TreinoResponse(
    Long id,
    Instant startsAt,
    String titulo,
    String descricao
) {}

