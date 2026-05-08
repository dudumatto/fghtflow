package com.fightflow.dto.evolucao;

import com.fightflow.entity.TipoEvolucao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record EvolucaoAlunoCreateRequest(
    @NotNull Long alunoId,
    @NotNull TipoEvolucao tipo,
    @NotNull @Size(max = 1000) String descricao,
    @NotNull Instant data
) {}

