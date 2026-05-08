package com.fightflow.dto.mensalidade;

import com.fightflow.entity.MetodoPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record MensalidadePagamentoRequest(
    @NotNull MetodoPagamento metodoPagamento,
    Instant dataPagamento,
    @Size(max = 120) String referencia
) {}

