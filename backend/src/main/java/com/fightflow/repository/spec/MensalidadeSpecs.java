package com.fightflow.repository.spec;

import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class MensalidadeSpecs {
  private MensalidadeSpecs() {}

  public static Specification<Mensalidade> academiaId(Long academiaId) {
    return (root, query, cb) -> academiaId == null ? cb.disjunction() : cb.equal(root.get("aluno").get("academia").get("id"), academiaId);
  }

  public static Specification<Mensalidade> alunoId(Long alunoId) {
    return (root, query, cb) -> alunoId == null ? cb.conjunction() : cb.equal(root.get("aluno").get("id"), alunoId);
  }

  public static Specification<Mensalidade> usuarioId(Long usuarioId) {
    return (root, query, cb) -> usuarioId == null ? cb.conjunction() : cb.equal(root.get("aluno").get("usuario").get("id"), usuarioId);
  }

  public static Specification<Mensalidade> status(MensalidadeStatus status) {
    return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
  }

  public static Specification<Mensalidade> vencimentoFrom(Instant from) {
    return (root, query, cb) -> from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("vencimento"), from);
  }

  public static Specification<Mensalidade> vencimentoTo(Instant to) {
    return (root, query, cb) -> to == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("vencimento"), to);
  }
}

