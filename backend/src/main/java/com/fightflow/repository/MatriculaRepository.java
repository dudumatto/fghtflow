package com.fightflow.repository;

import com.fightflow.entity.Matricula;
import com.fightflow.entity.MatriculaStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaRepository extends JpaRepository<Matricula, Long>, JpaSpecificationExecutor<Matricula> {
  @EntityGraph(attributePaths = {"aluno", "aluno.usuario", "aluno.academia", "plano", "plano.academia"})
  @Query("select m from Matricula m where m.id = :id")
  Optional<Matricula> findByIdWithAlunoAndPlano(@Param("id") Long id);

  List<Matricula> findAllByAlunoIdOrderByDataInicioDesc(Long alunoId);
  List<Matricula> findAllByAlunoIdAndStatusOrderByDataInicioDesc(Long alunoId, MatriculaStatus status);
  boolean existsByAlunoIdAndStatus(Long alunoId, MatriculaStatus status);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select m from Matricula m where m.aluno.id = :alunoId and m.status = :status")
  List<Matricula> findAllByAlunoIdAndStatusForUpdate(@Param("alunoId") Long alunoId, @Param("status") MatriculaStatus status);
}
