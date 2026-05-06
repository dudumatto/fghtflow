package com.fightflow.dto.atleta;

public record AtletaProfileResponse(
    Long atletaId,
    Long usuarioId,
    Long academiaId,
    String email,
    String faixa,
    Double peso,
    String categoria
) {}

