package com.fightflow.dto.dashboard;

import java.time.Instant;

public record AulasDashboardResponse(
    long upcomingAulas,
    long presencasPresentes,
    long presencasAusentes,
    long presencasJustificadas,
    Instant nextAulaAt
) {}

