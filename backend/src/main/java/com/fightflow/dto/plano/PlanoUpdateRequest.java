package com.fightflow.dto.plano;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PlanoUpdateRequest(
    @Size(max = 120) String nome,
    @Size(max = 1000) String descricao,
    @DecimalMin("0.00") BigDecimal valor,
    @Min(1) Integer duracaoEmDias,
    Boolean ativo
) {}

