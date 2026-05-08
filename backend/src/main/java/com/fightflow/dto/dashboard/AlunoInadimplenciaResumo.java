package com.fightflow.dto.dashboard;

import java.math.BigDecimal;

public record AlunoInadimplenciaResumo(
    Long alunoId,
    String nome,
    long mensalidadesEmAtraso,
    BigDecimal totalEmAtraso
) {}

