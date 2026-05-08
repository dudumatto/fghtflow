package com.fightflow.repository;

import com.fightflow.entity.Aluno;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
  Optional<Aluno> findByUsuarioId(Long usuarioId);

  @EntityGraph(attributePaths = {"usuario", "academia"})
  @Query("select a from Aluno a where a.usuario.id = :usuarioId")
  Optional<Aluno> findByUsuarioIdWithUsuarioAndAcademia(@Param("usuarioId") Long usuarioId);

  List<Aluno> findAllByAcademiaIdAndAtivoTrue(Long academiaId);

  long countByAcademiaIdAndAtivoTrue(Long academiaId);
  long countByAcademiaIdAndAtivoTrueAndCreatedAtGreaterThanEqual(Long academiaId, Instant from);
}
