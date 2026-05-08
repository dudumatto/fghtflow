package com.fightflow.dto.mensalidade;

import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.MetodoPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public record MensalidadeCreateRequest(
    @NotNull Long alunoId,
    @NotNull Long planoId,
    @NotNull @DecimalMin("0.00") BigDecimal valor,
    @NotNull Instant vencimento,
    Instant dataPagamento,
    MensalidadeStatus status,
    MetodoPagamento metodoPagamento,
    @Size(max = 120) String referencia
) {}

