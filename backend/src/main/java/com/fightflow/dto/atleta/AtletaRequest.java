package com.fightflow.dto.atleta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AtletaRequest(
    @Email @Size(max = 180) String email,
    @Size(min = 8, max = 72) String password,
    @Size(max = 120) String nome,
    Long academiaId,
    Boolean ativo,
    @Size(max = 30) String faixa,
    Integer grauAtual,
    Double peso,
    @Size(max = 60) String categoria
) {}
