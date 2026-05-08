package com.fightflow.repository;

import com.fightflow.entity.Graduacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraduacaoRepository extends JpaRepository<Graduacao, Long> {
  List<Graduacao> findAllByAlunoIdOrderByDataGraduacaoDesc(Long alunoId);
}

