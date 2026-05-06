package com.fightflow.repository;

import com.fightflow.entity.Presenca;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresencaRepository extends JpaRepository<Presenca, Long> {
  List<Presenca> findAllByTreinoId(Long treinoId);
}

