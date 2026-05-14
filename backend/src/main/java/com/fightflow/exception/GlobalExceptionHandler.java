package com.fightflow.exception;

import com.fightflow.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ApiResponse<Void>> handleMultipartTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest req) {
    return build(HttpStatus.PAYLOAD_TOO_LARGE, "File too large", req.getRequestURI());
  }

  @ExceptionHandler(UnsupportedMediaTypeException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnsupported(UnsupportedMediaTypeException ex, HttpServletRequest req) {
    return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  public ResponseEntity<ApiResponse<Void>> handleAuthFailure(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.UNAUTHORIZED, "Invalid credentials", req.getRequestURI());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
    String msg = "Data integrity violation";
    String raw = String.valueOf(ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
    // Best-effort mapping: avoid leaking DB internals but return the right status for common cases.
    if (raw.toLowerCase().contains("uk_usuario_email") || raw.toLowerCase().contains("usuarios") && raw.toLowerCase().contains("email")) {
      return build(HttpStatus.CONFLICT, "Email already in use", req.getRequestURI());
    }
    return build(HttpStatus.BAD_REQUEST, msg, req.getRequestURI());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, "Not found", req.getRequestURI());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, "Not found", req.getRequestURI());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(LazyInitializationException.class)
  public ResponseEntity<ApiResponse<Void>> handleLazy(LazyInitializationException ex, HttpServletRequest req) {
    log.error("hibernate.lazy_init path={} message={}", req.getRequestURI(), ex.getMessage());
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", req.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("Unhandled error path={} message={}", req.getRequestURI(), ex.getMessage(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", req.getRequestURI());
  }

  private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String message, String path) {
    if (status.is5xxServerError()) {
      log.error("api.error status={} path={} message={}", status.value(), path, message);
    } else if (status == HttpStatus.FORBIDDEN) {
      log.warn("api.forbidden path={} message={}", path, message);
    } else {
      log.info("api.client_error status={} path={} message={}", status.value(), path, message);
    }
    return ResponseEntity.status(status).body(ApiResponse.error(message));
  }
}
