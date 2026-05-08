package com.fightflow.repository;

import com.fightflow.entity.Plano;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlanoRepository extends JpaRepository<Plano, Long>, JpaSpecificationExecutor<Plano> {
  List<Plano> findAllByAtivoTrueOrderByNomeAsc();
}
