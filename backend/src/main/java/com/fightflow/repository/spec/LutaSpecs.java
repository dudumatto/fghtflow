package com.fightflow.repository.spec;

import com.fightflow.entity.Luta;
import com.fightflow.entity.LutaResultado;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class LutaSpecs {
  private LutaSpecs() {}

  public static Specification<Luta> atletaId(Long atletaId) {
    return (root, query, cb) -> atletaId == null ? cb.conjunction() : cb.equal(root.get("atleta").get("id"), atletaId);
  }

  public static Specification<Luta> resultado(LutaResultado resultado) {
    return (root, query, cb) -> resultado == null ? cb.conjunction() : cb.equal(root.get("resultado"), resultado);
  }

  public static Specification<Luta> foughtAtFrom(Instant from) {
    return (root, query, cb) -> from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("foughtAt"), from);
  }

  public static Specification<Luta> foughtAtTo(Instant to) {
    return (root, query, cb) -> to == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("foughtAt"), to);
  }

  public static Specification<Luta> academiaId(Long academiaId) {
    return (root, query, cb) -> academiaId == null
        ? cb.conjunction()
        : cb.equal(root.get("atleta").get("academia").get("id"), academiaId);
  }
}

