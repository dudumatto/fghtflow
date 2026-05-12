package com.fightflow.repository;

import com.fightflow.entity.Academia;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademiaRepository extends JpaRepository<Academia, Long> {
  @EntityGraph(attributePaths = {"professorResponsavel"})
  List<Academia> findAllByOrderByNomeAsc();

  @EntityGraph(attributePaths = {"professorResponsavel"})
  List<Academia> findAllByAtivoTrueOrderByNomeAsc();
}
