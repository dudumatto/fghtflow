package com.fightflow.repository.spec;

import com.fightflow.entity.Plano;
import org.springframework.data.jpa.domain.Specification;

public final class PlanoSpecs {
  private PlanoSpecs() {}

  public static Specification<Plano> academiaId(Long academiaId) {
    return (root, query, cb) -> academiaId == null ? cb.disjunction() : cb.equal(root.get("academia").get("id"), academiaId);
  }

  public static Specification<Plano> ativo(Boolean ativo) {
    return (root, query, cb) -> ativo == null ? cb.conjunction() : cb.equal(root.get("ativo"), ativo);
  }
}

