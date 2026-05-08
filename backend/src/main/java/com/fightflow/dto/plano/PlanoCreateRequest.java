package com.fightflow.dto.plano;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PlanoCreateRequest(
    @NotBlank @Size(max = 120) String nome,
    @Size(max = 1000) String descricao,
    @NotNull @DecimalMin("0.00") BigDecimal valor,
    @Min(1) int duracaoEmDias,
    Boolean ativo
) {}

