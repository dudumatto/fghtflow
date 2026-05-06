package com.fightflow.repository;

import com.fightflow.entity.Treino;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TreinoRepository extends JpaRepository<Treino, Long>, JpaSpecificationExecutor<Treino> {
  List<Treino> findAllByAcademiaIdOrderByStartsAtDesc(Long academiaId);
}
