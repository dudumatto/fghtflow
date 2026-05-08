package com.fightflow.repository.spec;

import com.fightflow.entity.Aula;
import com.fightflow.entity.AulaTipo;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class AulaSpecs {
  private AulaSpecs() {}

  public static Specification<Aula> academiaId(Long academiaId) {
    return (root, query, cb) -> academiaId == null ? cb.conjunction() : cb.equal(root.get("academia").get("id"), academiaId);
  }

  public static Specification<Aula> professorUsuarioId(Long professorUsuarioId) {
    return (root, query, cb) -> professorUsuarioId == null ? cb.conjunction() : cb.equal(root.get("professor").get("id"), professorUsuarioId);
  }

  public static Specification<Aula> tipo(AulaTipo tipo) {
    return (root, query, cb) -> tipo == null ? cb.conjunction() : cb.equal(root.get("tipo"), tipo);
  }

  public static Specification<Aula> ativa(Boolean ativa) {
    return (root, query, cb) -> ativa == null ? cb.conjunction() : cb.equal(root.get("ativa"), ativa);
  }

  public static Specification<Aula> inicioFrom(Instant from) {
    return (root, query, cb) -> from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("dataHoraInicio"), from);
  }

  public static Specification<Aula> inicioTo(Instant to) {
    return (root, query, cb) -> to == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("dataHoraInicio"), to);
  }

  public static Specification<Aula> tituloLike(String q) {
    String trimmed = String.valueOf(q == null ? "" : q).trim();
    if (trimmed.isEmpty()) return (root, query, cb) -> cb.conjunction();
    String like = "%" + trimmed.toLowerCase() + "%";
    return (root, query, cb) -> cb.like(cb.lower(root.get("titulo")), like);
  }
}

