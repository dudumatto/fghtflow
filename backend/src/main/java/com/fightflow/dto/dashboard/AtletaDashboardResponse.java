package com.fightflow.dto.dashboard;

import com.fightflow.dto.financeiro.FinanceiroStatusResponse;

public record AtletaDashboardResponse(
    long totalFights,
    long wins,
    long losses,
    double winrate,
    double submissionRate,
    FinanceiroStatusResponse financeiro
) {}
