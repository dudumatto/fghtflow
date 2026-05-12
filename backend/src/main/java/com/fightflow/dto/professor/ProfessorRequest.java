package com.fightflow.dto.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ProfessorRequest(
    @Email @Size(max = 180) String email,
    @Size(min = 8, max = 72) String password,
    Long academiaId
) {}
