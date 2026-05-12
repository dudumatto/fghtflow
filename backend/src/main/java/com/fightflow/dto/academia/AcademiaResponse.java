package com.fightflow.dto.academia;

import java.time.Instant;

public record AcademiaResponse(
    Long id,
    String nome,
    String endereco,
    boolean ativo,
    Long professorResponsavelId,
    String professorResponsavelNome,
    Instant createdAt,
    Instant updatedAt
) {}
