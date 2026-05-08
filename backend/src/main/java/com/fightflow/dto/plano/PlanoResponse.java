package com.fightflow.dto.plano;

import java.math.BigDecimal;
import java.time.Instant;

public record PlanoResponse(
    Long id,
    Long academiaId,
    String nome,
    String descricao,
    BigDecimal valor,
    int duracaoEmDias,
    boolean ativo,
    Instant createdAt
) {}

