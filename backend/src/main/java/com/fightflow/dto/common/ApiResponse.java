package com.fightflow.dto.common;

public record ApiResponse<T>(
    boolean success,
    T data,
    Object error
) {
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, data, null);
  }

  public static ApiResponse<Void> ok() {
    return new ApiResponse<>(true, null, null);
  }

  public static ApiResponse<Void> error(Object error) {
    return new ApiResponse<>(false, null, error);
  }
}

