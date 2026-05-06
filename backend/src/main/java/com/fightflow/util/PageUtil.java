package com.fightflow.util;

import com.fightflow.dto.common.ApiPage;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public final class PageUtil {
  private PageUtil() {}

  public static <T, R> ApiPage<R> toApiPage(Page<T> page, Function<T, R> mapper) {
    return new ApiPage<>(
        page.getContent().stream().map(mapper).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.hasNext(),
        page.hasPrevious()
    );
  }
}

