package com.fightflow.repository.spec;

import com.fightflow.entity.Matricula;
import com.fightflow.entity.MatriculaStatus;
import org.springframework.data.jpa.domain.Specification;

public final class MatriculaSpecs {
  private MatriculaSpecs() {}

  public static Specification<Matricula> academiaId(Long academiaId) {
    return (root, query, cb) -> academiaId == null ? cb.disjunction() : cb.equal(root.get("aluno").get("academia").get("id"), academiaId);
  }

  public static Specification<Matricula> alunoId(Long alunoId) {
    return (root, query, cb) -> alunoId == null ? cb.conjunction() : cb.equal(root.get("aluno").get("id"), alunoId);
  }

  public static Specification<Matricula> usuarioId(Long usuarioId) {
    return (root, query, cb) -> usuarioId == null ? cb.conjunction() : cb.equal(root.get("aluno").get("usuario").get("id"), usuarioId);
  }

  public static Specification<Matricula> status(MatriculaStatus status) {
    return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
  }
}

