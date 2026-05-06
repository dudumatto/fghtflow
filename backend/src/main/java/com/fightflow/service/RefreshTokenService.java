package com.fightflow.service;

import com.fightflow.entity.RefreshToken;
import com.fightflow.entity.Usuario;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.repository.RefreshTokenRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.JwtService;
import com.fightflow.security.UserPrincipal;
import com.fightflow.util.TokenUtil;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
  public record Issued(String accessToken, String refreshToken) {}
  public record Refreshed(Usuario user, Issued issued) {}

  private final RefreshTokenRepository refreshTokenRepository;
  private final UsuarioRepository usuarioRepository;
  private final JwtService jwtService;
  private final long refreshTtlDays;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      UsuarioRepository usuarioRepository,
      JwtService jwtService,
      @Value("${fightflow.jwt.refreshTtlDays:14}") long refreshTtlDays
  ) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.usuarioRepository = usuarioRepository;
    this.jwtService = jwtService;
    this.refreshTtlDays = refreshTtlDays;
  }

  @Transactional
  public Issued issueForUser(Usuario u) {
    Long academiaId = (u.getAcademia() == null) ? null : u.getAcademia().getId();
    UserPrincipal principal = new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), academiaId);
    String access = jwtService.generate(principal);
    String refresh = TokenUtil.newRefreshToken();

    RefreshToken rt = new RefreshToken();
    rt.setUsuario(usuarioRepository.getReferenceById(u.getId()));
    rt.setTokenHash(TokenUtil.sha256Hex(refresh));
    rt.setExpiresAt(Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS));
    refreshTokenRepository.save(rt);

    return new Issued(access, refresh);
  }

  @Transactional
  public Refreshed refresh(String refreshTokenRaw) {
    if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
      throw new ForbiddenException("Missing refresh token");
    }
    String hash = TokenUtil.sha256Hex(refreshTokenRaw);
    RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
        .orElseThrow(() -> new ForbiddenException("Invalid refresh token"));

    if (existing.getRevokedAt() != null || existing.getExpiresAt().isBefore(Instant.now())) {
      throw new ForbiddenException("Refresh token expired or revoked");
    }

    // Rotate token: revoke old, issue new.
    existing.setRevokedAt(Instant.now());
    refreshTokenRepository.save(existing);

    Usuario u = usuarioRepository.findById(existing.getUsuario().getId())
        .orElseThrow(() -> new ForbiddenException("Invalid refresh token"));
    return new Refreshed(u, issueForUser(u));
  }

  public Long validateAndGetUserId(String refreshTokenRaw) {
    if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
      throw new ForbiddenException("Missing refresh token");
    }
    String hash = TokenUtil.sha256Hex(refreshTokenRaw);
    RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
        .orElseThrow(() -> new ForbiddenException("Invalid refresh token"));
    if (existing.getRevokedAt() != null || existing.getExpiresAt().isBefore(Instant.now())) {
      throw new ForbiddenException("Refresh token expired or revoked");
    }
    return existing.getUsuario().getId();
  }

  @Transactional
  public void revokeAllForUser(Long usuarioId) {
    refreshTokenRepository.revokeAllForUser(usuarioId, Instant.now());
  }
}
