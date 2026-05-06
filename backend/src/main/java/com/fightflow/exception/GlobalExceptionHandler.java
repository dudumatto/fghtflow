package com.fightflow.exception;

import com.fightflow.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    StringBuilder msg = new StringBuilder("Validation failed");
    FieldError fe = ex.getBindingResult().getFieldError();
    if (fe != null) {
      msg = new StringBuilder(fe.getField()).append(": ").append(fe.getDefaultMessage());
    }
    return build(HttpStatus.BAD_REQUEST, msg.toString(), req.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, "Forbidden", req.getRequestURI());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(PayloadTooLargeException.class)
  public ResponseEntity<ApiResponse<Void>> handleTooLarge(PayloadTooLargeException ex, HttpServletRequest req) {
    return build(HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(UnsupportedMediaTypeException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnsupported(UnsupportedMediaTypeException ex, HttpServletRequest req) {
    return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  public ResponseEntity<ApiResponse<Void>> handleAuthFailure(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.UNAUTHORIZED, "Invalid credentials", req.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("Unhandled error path={} message={}", req.getRequestURI(), ex.getMessage(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", req.getRequestURI());
  }

  private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String message, String path) {
    ErrorEnvelope err = new ErrorEnvelope(Instant.now(), status.value(), message, path);
    if (status.is5xxServerError()) {
      log.error("api.error status={} path={} message={}", status.value(), path, message);
    } else if (status == HttpStatus.FORBIDDEN) {
      log.warn("api.forbidden path={} message={}", path, message);
    } else {
      log.info("api.client_error status={} path={} message={}", status.value(), path, message);
    }
    return ResponseEntity.status(status).body(new ApiResponse<>(false, null, err));
  }
}
