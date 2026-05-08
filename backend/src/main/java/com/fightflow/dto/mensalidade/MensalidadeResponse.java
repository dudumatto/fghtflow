package com.fightflow.dto.mensalidade;

import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.MetodoPagamento;
import java.math.BigDecimal;
import java.time.Instant;

public record MensalidadeResponse(
    Long id,
    Long alunoId,
    Long planoId,
    BigDecimal valor,
    Instant vencimento,
    Instant dataPagamento,
    MensalidadeStatus status,
    MetodoPagamento metodoPagamento,
    String referencia,
    Instant createdAt
) {}

