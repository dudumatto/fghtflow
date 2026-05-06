package com.fightflow.dto.competicao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CompeticaoCreateRequest(
    @NotBlank @Size(max = 140) String nome,
    @Size(max = 140) String local,
    @NotNull Instant startsAt
) {}

