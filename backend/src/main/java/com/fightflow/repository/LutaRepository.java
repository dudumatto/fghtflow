package com.fightflow.repository;

import com.fightflow.entity.Luta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LutaRepository extends JpaRepository<Luta, Long>, JpaSpecificationExecutor<Luta> {
  List<Luta> findAllByAtletaIdOrderByFoughtAtDesc(Long atletaId);

  @Query("select count(l) from Luta l where l.atleta.id = :atletaId")
  long countByAtletaId(@Param("atletaId") Long atletaId);

  @Query("select count(l) from Luta l where l.atleta.id = :atletaId and l.resultado = 'WIN'")
  long countWins(@Param("atletaId") Long atletaId);

  @Query("select count(l) from Luta l where l.atleta.id = :atletaId and l.resultado = 'LOSS'")
  long countLosses(@Param("atletaId") Long atletaId);

  @Query("select count(l) from Luta l where l.atleta.id = :atletaId and l.resultado = 'WIN' and l.metodo = 'SUBMISSION'")
  long countSubmissionWins(@Param("atletaId") Long atletaId);
}
