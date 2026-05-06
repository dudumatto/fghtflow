package com.fightflow.exception;

import java.time.Instant;

public record ErrorEnvelope(
    Instant timestamp,
    int status,
    String message,
    String path
) {}

