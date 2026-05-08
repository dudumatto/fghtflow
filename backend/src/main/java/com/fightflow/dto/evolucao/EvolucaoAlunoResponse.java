package com.fightflow.dto.evolucao;

import com.fightflow.entity.TipoEvolucao;
import java.time.Instant;

public record EvolucaoAlunoResponse(
    Long id,
    Long alunoId,
    TipoEvolucao tipo,
    String descricao,
    Instant data,
    Long professorUsuarioId
) {}

