package com.fightflow.repository;

import com.fightflow.entity.Atleta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtletaRepository extends JpaRepository<Atleta, Long> {
  Optional<Atleta> findByUsuarioId(Long usuarioId);
  Optional<Atleta> findByAlunoId(Long alunoId);

  @Query("""
      select a
      from Atleta a
      join fetch a.usuario u
      join fetch a.academia
      left join fetch a.aluno
      left join fetch u.academia
      where u.id = :usuarioId
      """)
  Optional<Atleta> findByUsuarioIdWithUsuarioAndAcademia(@Param("usuarioId") Long usuarioId);

  List<Atleta> findAllByAcademiaId(Long academiaId);
  List<Atleta> findAllByAcademiaIdInOrderByUsuarioEmailAsc(List<Long> academiaIds);
  List<Atleta> findAllByAcademiaIdInAndAtivoTrueOrderByUsuarioEmailAsc(List<Long> academiaIds);
  List<Atleta> findAllByAtivoTrueOrderByUsuarioEmailAsc();
  List<Atleta> findAllByOrderByUsuarioEmailAsc();
}
