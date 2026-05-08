package com.fightflow.repository;

import com.fightflow.entity.EvolucaoAluno;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvolucaoAlunoRepository extends JpaRepository<EvolucaoAluno, Long> {
  List<EvolucaoAluno> findAllByAlunoIdOrderByDataDesc(Long alunoId);
}

