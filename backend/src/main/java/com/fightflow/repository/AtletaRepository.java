package com.fightflow.repository;

import com.fightflow.entity.Atleta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtletaRepository extends JpaRepository<Atleta, Long> {
  Optional<Atleta> findByUsuarioId(Long usuarioId);
  List<Atleta> findAllByAcademiaId(Long academiaId);
}

