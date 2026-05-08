package com.fightflow.dto.graduacao;

import com.fightflow.entity.Faixa;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record GraduacaoCreateRequest(
    @NotNull Long alunoId,
    @NotNull Faixa faixa,
    @Min(0) int grau,
    @NotNull Instant dataGraduacao,
    @Size(max = 1000) String observacao
) {}

