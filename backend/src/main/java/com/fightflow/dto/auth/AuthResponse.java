package com.fightflow.dto.auth;

import com.fightflow.entity.Role;

public record AuthResponse(
    String token,
    Long usuarioId,
    Role role,
    Long academiaId
) {}

