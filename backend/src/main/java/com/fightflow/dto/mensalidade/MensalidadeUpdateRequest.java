package com.fightflow.dto.mensalidade;

import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.MetodoPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public record MensalidadeUpdateRequest(
    Long planoId,
    @DecimalMin("0.00") BigDecimal valor,
    Instant vencimento,
    Instant dataPagamento,
    MensalidadeStatus status,
    MetodoPagamento metodoPagamento,
    @Size(max = 120) String referencia
) {}

