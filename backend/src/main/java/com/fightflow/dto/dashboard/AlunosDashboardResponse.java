package com.fightflow.dto.dashboard;

import java.time.Instant;
import java.util.List;

public record AlunosDashboardResponse(
    long alunosAtivos,
    long alunosNovos30d,
    long alunosComInadimplenciaBloqueante,
    List<AlunoInadimplenciaResumo> topInadimplentes,
    Instant generatedAt
) {}

