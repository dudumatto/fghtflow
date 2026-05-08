package com.fightflow.repository;

import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.dto.dashboard.AlunoInadimplenciaResumo;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

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

  long countByAlunoAcademiaIdAndStatus(Long academiaId, MensalidadeStatus status);

  @Query("""
      select coalesce(sum(m.valor), 0)
      from Mensalidade m
      where m.aluno.academia.id = :academiaId
        and m.status = :status
      """)
  BigDecimal sumValorByAcademiaIdAndStatus(@Param("academiaId") Long academiaId, @Param("status") MensalidadeStatus status);

  @Query("""
      select count(m)
      from Mensalidade m
      where m.aluno.academia.id = :academiaId
        and m.status = :status
        and m.dataPagamento >= :from
        and m.dataPagamento < :to
      """)
  long countPagasByAcademiaIdBetween(@Param("academiaId") Long academiaId, @Param("status") MensalidadeStatus status, @Param("from") Instant from, @Param("to") Instant to);

  @Query("""
      select coalesce(sum(m.valor), 0)
      from Mensalidade m
      where m.aluno.academia.id = :academiaId
        and m.status = :status
        and m.dataPagamento >= :from
        and m.dataPagamento < :to
      """)
  BigDecimal sumValorByAcademiaIdAndStatusBetween(@Param("academiaId") Long academiaId, @Param("status") MensalidadeStatus status, @Param("from") Instant from, @Param("to") Instant to);

  @Query("""
      select count(distinct m.aluno.id)
      from Mensalidade m
      where m.aluno.academia.id = :academiaId
        and m.status in :statuses
        and m.vencimento < :cutoff
      """)
  long countDistinctAlunosInadimplenciaBloqueante(
      @Param("academiaId") Long academiaId,
      @Param("statuses") Collection<MensalidadeStatus> statuses,
      @Param("cutoff") Instant cutoff
  );

  @Query("""
      select new com.fightflow.dto.dashboard.AlunoInadimplenciaResumo(
        a.id,
        a.nome,
        count(m),
        coalesce(sum(m.valor), 0)
      )
      from Mensalidade m
      join m.aluno a
      where a.academia.id = :academiaId
        and m.status in :statuses
        and m.vencimento < :now
      group by a.id, a.nome
      order by sum(m.valor) desc
      """)
  List<AlunoInadimplenciaResumo> topInadimplentesByAcademia(
      @Param("academiaId") Long academiaId,
      @Param("statuses") Collection<MensalidadeStatus> statuses,
      @Param("now") Instant now,
      Pageable pageable
  );
}
