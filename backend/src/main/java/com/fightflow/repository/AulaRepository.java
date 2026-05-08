package com.fightflow.repository;

import com.fightflow.entity.Aula;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AulaRepository extends JpaRepository<Aula, Long>, JpaSpecificationExecutor<Aula> {
  List<Aula> findAllByAcademiaIdOrderByDataHoraInicioDesc(Long academiaId);

  Aula findFirstByAcademiaIdAndAtivaTrueAndDataHoraInicioGreaterThanEqualOrderByDataHoraInicioAsc(Long academiaId, Instant now);

  long countByAcademiaIdAndAtivaTrueAndDataHoraInicioGreaterThanEqual(Long academiaId, Instant now);
}
