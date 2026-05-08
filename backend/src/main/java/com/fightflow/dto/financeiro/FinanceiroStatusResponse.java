package com.fightflow.dto.financeiro;

public record FinanceiroStatusResponse(
    boolean bloqueado,
    String status,
    int diasToleranciaInadimplencia
) {}

