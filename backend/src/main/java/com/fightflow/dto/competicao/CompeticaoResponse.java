package com.fightflow.dto.competicao;

import java.time.Instant;

public record CompeticaoResponse(
    Long id,
    String nome,
    String local,
    Instant startsAt
) {}

