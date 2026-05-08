package com.fightflow.service;

import com.fightflow.dto.presenca.PresencaCreateRequest;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Presenca;
import com.fightflow.entity.Role;
import com.fightflow.entity.Treino;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.PresencaRepository;
import com.fightflow.repository.TreinoRepository;
import com.fightflow.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PresencaService {
  private final PresencaRepository presencaRepository;
  private final TreinoRepository treinoRepository;
  private final AtletaRepository atletaRepository;
  private final FinanceiroBloqueioService financeiroBloqueioService;

  public PresencaService(
      PresencaRepository presencaRepository,
      TreinoRepository treinoRepository,
      AtletaRepository atletaRepository,
      FinanceiroBloqueioService financeiroBloqueioService
  ) {
    this.presencaRepository = presencaRepository;
    this.treinoRepository = treinoRepository;
    this.atletaRepository = atletaRepository;
    this.financeiroBloqueioService = financeiroBloqueioService;
  }

  @Transactional
  public void registrar(UserPrincipal me, PresencaCreateRequest req) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can register attendance");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }

    Treino treino = treinoRepository.findById(req.treinoId()).orElseThrow(() -> new NotFoundException("Treino not found"));
    if (!treino.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Treino is not in your academia");
    }

    Atleta atleta = atletaRepository.findById(req.atletaId()).orElseThrow(() -> new NotFoundException("Atleta not found"));
    if (!atleta.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Atleta is not in your academia");
    }
    financeiroBloqueioService.assertPodeRegistrarPresenca(atleta.getAluno());

    Presenca p = new Presenca();
    p.setTreino(treino);
    p.setAtleta(atleta);
    presencaRepository.save(p);
  }
}
