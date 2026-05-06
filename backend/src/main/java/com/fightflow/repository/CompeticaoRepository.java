package com.fightflow.repository;

import com.fightflow.entity.Competicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompeticaoRepository extends JpaRepository<Competicao, Long>, JpaSpecificationExecutor<Competicao> {
  List<Competicao> findAllByAcademiaIdOrderByStartsAtDesc(Long academiaId);
}
