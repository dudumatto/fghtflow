package com.fightflow.dto.academia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcademiaRequest(
    @NotBlank @Size(max = 120) String nome,
    @Size(max = 240) String endereco,
    Boolean ativo,
    Long professorResponsavelId
) {}
