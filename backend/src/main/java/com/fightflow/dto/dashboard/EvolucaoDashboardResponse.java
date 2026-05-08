package com.fightflow.dto.dashboard;

import com.fightflow.entity.Faixa;
import java.time.Instant;
import java.util.List;

public record EvolucaoDashboardResponse(
    Long alunoId,
    Faixa faixaAtual,
    int grauAtual,
    Instant ultimaGraduacaoEm,
    long totalEvolucoes,
    List<String> recomendacoes
) {}

