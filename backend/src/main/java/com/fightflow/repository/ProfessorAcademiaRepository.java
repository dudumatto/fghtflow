package com.fightflow.repository;

import com.fightflow.entity.Academia;
import com.fightflow.entity.ProfessorAcademia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorAcademiaRepository extends JpaRepository<ProfessorAcademia, Long> {
  boolean existsByProfessorIdAndAcademiaIdAndAtivoTrue(Long professorId, Long academiaId);

  Optional<ProfessorAcademia> findByProfessorIdAndAcademiaId(Long professorId, Long academiaId);

  @EntityGraph(attributePaths = {"academia", "academia.professorResponsavel"})
  List<ProfessorAcademia> findAllByProfessorIdAndAtivoTrueOrderByAcademiaNomeAsc(Long professorId);

  @EntityGraph(attributePaths = {"academia", "academia.professorResponsavel"})
  List<ProfessorAcademia> findAllByProfessorIdOrderByAcademiaNomeAsc(Long professorId);

  List<ProfessorAcademia> findAllByAcademiaIdAndAtivoTrue(Long academiaId);

  default List<Academia> findAcademiasAtivasByProfessorId(Long professorId) {
    return findAllByProfessorIdAndAtivoTrueOrderByAcademiaNomeAsc(professorId).stream()
        .map(ProfessorAcademia::getAcademia)
        .toList();
  }
}
