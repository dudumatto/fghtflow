package com.fightflow.dto.atleta;

import com.fightflow.dto.financeiro.FinanceiroStatusResponse;

public record AtletaProfileResponse(
    Long atletaId,
    Long usuarioId,
    Long academiaId,
    String email,
    String faixa,
    Double peso,
    String categoria,
    FinanceiroStatusResponse financeiro
) {}
