package com.fightflow.repository;

import com.fightflow.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  @Modifying
  @Query("update RefreshToken rt set rt.revokedAt = :now where rt.usuario.id = :usuarioId and rt.revokedAt is null")
  int revokeAllForUser(@Param("usuarioId") Long usuarioId, @Param("now") Instant now);
}

