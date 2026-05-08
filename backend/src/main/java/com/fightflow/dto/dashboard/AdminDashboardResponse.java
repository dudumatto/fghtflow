package com.fightflow.dto.dashboard;

import java.time.Instant;

public record AdminDashboardResponse(
    long alunosAtivos,
    long usuariosAtletas,
    long usuariosProfessores,
    long usuariosAdmins,
    long planosAtivos,
    long matriculasAtivas,
    long matriculasBloqueadas,
    long aulasProximas,
    Instant generatedAt
) {}

