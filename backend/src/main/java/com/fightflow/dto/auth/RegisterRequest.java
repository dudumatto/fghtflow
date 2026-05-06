package com.fightflow.dto.auth;

import com.fightflow.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email @NotBlank @Size(max = 180) String email,
    @NotBlank @Size(min = 8, max = 72) String password,
    Role role,
    Long academiaId,
    @Size(max = 120) String academiaNome,
    @Size(max = 30) String faixa,
    Double peso,
    @Size(max = 60) String categoria
) {}
