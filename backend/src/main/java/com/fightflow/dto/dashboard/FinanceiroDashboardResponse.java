package com.fightflow.dto.dashboard;

import java.math.BigDecimal;
import java.time.Instant;

public record FinanceiroDashboardResponse(
    long mensalidadesPendentes,
    long mensalidadesAtrasadas,
    long mensalidadesPagasNoMes,
    BigDecimal totalPendente,
    BigDecimal totalAtrasado,
    BigDecimal receitaNoMes,
    long alunosComInadimplenciaBloqueante,
    int diasToleranciaInadimplencia,
    Instant generatedAt
) {}

