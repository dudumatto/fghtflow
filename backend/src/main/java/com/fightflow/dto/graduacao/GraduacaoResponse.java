package com.fightflow.dto.graduacao;

import com.fightflow.entity.Faixa;
import java.time.Instant;

public record GraduacaoResponse(
    Long id,
    Long alunoId,
    Faixa faixa,
    int grau,
    Instant dataGraduacao,
    Long professorUsuarioId,
    String observacao,
    boolean foraDeOrdem
) {}

