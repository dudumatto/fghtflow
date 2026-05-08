package com.fightflow.repository;

import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long>, JpaSpecificationExecutor<Mensalidade> {
  @EntityGraph(attributePaths = {"aluno", "aluno.usuario", "aluno.academia", "plano", "plano.academia"})
  @Query("select m from Mensalidade m where m.id = :id")
  Optional<Mensalidade> findByIdWithAlunoAndPlano(@Param("id") Long id);

  List<Mensalidade> findAllByAlunoIdOrderByVencimentoDesc(Long alunoId);
  List<Mensalidade> findAllByAlunoIdAndStatusOrderByVencimentoDesc(Long alunoId, MensalidadeStatus status);

  @Query("""
      select count(m) > 0
      from Mensalidade m
      where m.aluno.id = :alunoId
        and m.status in :statuses
        and m.vencimento < :cutoff
      """)
  boolean existsInadimplenciaBloqueante(
      @Param("alunoId") Long alunoId,
      @Param("statuses") Collection<MensalidadeStatus> statuses,
      @Param("cutoff") Instant cutoff
  );
}
