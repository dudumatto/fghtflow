package com.fightflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final String issuer;
  private final long ttlMinutes;

  public JwtService(
      @Value("${fightflow.jwt.secret}") String secret,
      @Value("${fightflow.jwt.issuer}") String issuer,
      @Value("${fightflow.jwt.ttlMinutes}") long ttlMinutes
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.ttlMinutes = ttlMinutes;
  }

  public String generate(UserPrincipal principal) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(ttlMinutes * 60L);
    return Jwts.builder()
        .issuer(issuer)
        .subject(principal.getUsername())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claim("uid", principal.getId())
        .claim("role", principal.getRole().name())
        .claim("academiaId", principal.getAcademiaId())
        .signWith(key)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(token);
  }
}

