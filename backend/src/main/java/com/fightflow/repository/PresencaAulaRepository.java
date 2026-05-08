package com.fightflow.repository;

import com.fightflow.entity.PresencaAula;
import com.fightflow.entity.PresencaAulaStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresencaAulaRepository extends JpaRepository<PresencaAula, Long> {
  List<PresencaAula> findAllByAulaId(Long aulaId);
  List<PresencaAula> findAllByAlunoIdOrderByRegistradaEmDesc(Long alunoId);
  Optional<PresencaAula> findByAulaIdAndAlunoId(Long aulaId, Long alunoId);

  long countByAulaIdAndStatus(Long aulaId, PresencaAulaStatus status);
  long countByAlunoIdAndStatus(Long alunoId, PresencaAulaStatus status);
  long countByAulaAcademiaIdAndStatus(Long academiaId, PresencaAulaStatus status);
}
