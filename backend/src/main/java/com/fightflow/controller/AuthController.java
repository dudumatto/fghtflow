package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.auth.AuthResponse;
import com.fightflow.dto.auth.LoginRequest;
import com.fightflow.dto.auth.RegisterRequest;
import com.fightflow.entity.Role;
import com.fightflow.service.AuthService;
import com.fightflow.service.RefreshTokenService;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;
  private final boolean refreshCookieSecure;
  private final long refreshTtlDays;

  public AuthController(
      AuthService authService,
      RefreshTokenService refreshTokenService,
      @Value("${fightflow.jwt.refreshCookieSecure:false}") boolean refreshCookieSecure,
      @Value("${fightflow.jwt.refreshTtlDays:14}") long refreshTtlDays
  ) {
    this.authService = authService;
    this.refreshTokenService = refreshTokenService;
    this.refreshCookieSecure = refreshCookieSecure;
    this.refreshTtlDays = refreshTtlDays;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
    // If client omitted role, fail fast with a consistent message.
    if (req.role() == null) {
      req = new RegisterRequest(req.email(), req.password(), Role.ATLETA, req.academiaId(), req.academiaNome(),
          req.faixa(), req.peso(), req.categoria());
    }
    AuthService.AuthPair pair = authService.register(req);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshCookie(pair.refreshToken()))
        .body(ApiResponse.ok(pair.auth()));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
    AuthService.AuthPair pair = authService.login(req);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshCookie(pair.refreshToken()))
        .body(ApiResponse.ok(pair.auth()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refresh(@CookieValue(name = "ff_refresh", required = false) String refreshToken) {
    RefreshTokenService.Refreshed refreshed = refreshTokenService.refresh(refreshToken);
    var u = refreshed.user();
    Long academiaId = u.getAcademia() == null ? null : u.getAcademia().getId();
    String academiaNome = u.getAcademia() == null ? null : u.getAcademia().getNome();
    AuthResponse auth = new AuthResponse(refreshed.issued().accessToken(), u.getId(), u.getRole(), academiaId, academiaNome);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshCookie(refreshed.issued().refreshToken()))
        .body(ApiResponse.ok(auth));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "ff_refresh", required = false) String refreshToken) {
    // Best-effort: if refresh token is valid, revoke all for that user. Otherwise just clear cookie.
    try {
      Long userId = refreshTokenService.validateAndGetUserId(refreshToken);
      refreshTokenService.revokeAllForUser(userId);
    } catch (Exception ignored) {
    }
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, clearRefreshCookie())
        .body(ApiResponse.ok());
  }

  private String refreshCookie(String raw) {
    ResponseCookie cookie = ResponseCookie.from("ff_refresh", raw)
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .path("/auth")
        .sameSite("Strict")
        .maxAge(Duration.ofDays(refreshTtlDays))
        .build();
    return cookie.toString();
  }

  private String clearRefreshCookie() {
    ResponseCookie cookie = ResponseCookie.from("ff_refresh", "")
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .path("/auth")
        .sameSite("Strict")
        .maxAge(Duration.ZERO)
        .build();
    return cookie.toString();
  }
}
