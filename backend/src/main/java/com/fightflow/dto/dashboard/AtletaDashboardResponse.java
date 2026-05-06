package com.fightflow.dto.dashboard;

public record AtletaDashboardResponse(
    long totalFights,
    long wins,
    long losses,
    double winrate,
    double submissionRate
) {}

