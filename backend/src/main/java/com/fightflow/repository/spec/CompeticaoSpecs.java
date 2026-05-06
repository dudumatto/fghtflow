package com.fightflow.repository.spec;

import com.fightflow.entity.Competicao;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class CompeticaoSpecs {
  private CompeticaoSpecs() {}

  public static Specification<Competicao> academiaId(Long academiaId) {
    return (root, query, cb) -> academiaId == null ? cb.conjunction() : cb.equal(root.get("academia").get("id"), academiaId);
  }

  public static Specification<Competicao> startsAtFrom(Instant from) {
    return (root, query, cb) -> from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("startsAt"), from);
  }

  public static Specification<Competicao> startsAtTo(Instant to) {
    return (root, query, cb) -> to == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("startsAt"), to);
  }
}

