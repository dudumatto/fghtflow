package com.fightflow.dto.common;

import java.util.List;

public record ApiPage<T>(
    List<T> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrev
) {}

