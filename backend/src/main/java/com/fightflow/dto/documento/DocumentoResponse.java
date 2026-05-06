package com.fightflow.dto.documento;

import java.time.Instant;

public record DocumentoResponse(
    Long id,
    String originalName,
    String mimeType,
    long sizeBytes,
    Instant createdAt
) {}

