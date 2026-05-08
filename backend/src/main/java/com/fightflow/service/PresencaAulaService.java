package com.fightflow.service;

import com.fightflow.dto.aula.PresencaAulaCreateRequest;
import com.fightflow.dto.aula.PresencaAulaResponse;
import com.fightflow.dto.aula.PresencaAulaUpdateRequest;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Aula;
import com.fightflow.entity.PresencaAula;
import com.fightflow.entity.PresencaAulaStatus;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ConflictException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.AulaRepository;
import com.fightflow.repository.PresencaAulaRepository;
import com.fightflow.security.UserPrincipal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PresencaAulaService {
  private final PresencaAulaRepository presencaAulaRepository;
  private final AulaRepository aulaRepository;
  private final AlunoRepository alunoRepository;
  private final FinanceiroBloqueioService financeiroBloqueioService;

  public PresencaAulaService(
      PresencaAulaRepository presencaAulaRepository,
      AulaRepository aulaRepository,
      AlunoRepository alunoRepository,
      FinanceiroBloqueioService financeiroBloqueioService
  ) {
    this.presencaAulaRepository = presencaAulaRepository;
    this.aulaRepository = aulaRepository;
    this.alunoRepository = alunoRepository;
    this.financeiroBloqueioService = financeiroBloqueioService;
  }

  public List<PresencaAulaResponse> list(UserPrincipal me, Long aulaId) {
    Aula aula = loadAndAssertInAcademia(me, aulaId);

    if (me.getRole() == Role.PROFESSOR || me.getRole() == Role.ADMIN) {
      return presencaAulaRepository.findAllByAulaId(aula.getId()).stream().map(this::toResponse).toList();
    }

    if (me.getRole() == Role.ATLETA) {
      Aluno aluno = alunoRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
          .orElseThrow(() -> new NotFoundException("Aluno not found"));
      if (!aluno.getAcademia().getId().equals(me.getAcademiaId())) {
        throw new ForbiddenException("Aluno is not in your academia");
      }
      return presencaAulaRepository.findByAulaIdAndAlunoId(aula.getId(), aluno.getId())
          .map(p -> List.of(toResponse(p))).orElseGet(List::of);
    }

    throw new ForbiddenException("Forbidden");
  }

  @Transactional
  public PresencaAulaResponse create(UserPrincipal me, Long aulaId, PresencaAulaCreateRequest req) {
    assertProfessorOrAdmin(me);
    Aula aula = loadAndAssertInAcademia(me, aulaId);

    Aluno aluno = alunoRepository.findById(req.alunoId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
    if (!aluno.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aluno is not in your academia");
    }

    PresencaAulaStatus status = req.status() == null ? PresencaAulaStatus.PRESENTE : req.status();
    if (status == PresencaAulaStatus.PRESENTE) {
      financeiroBloqueioService.assertPodeRegistrarPresenca(aluno);
      assertCapacidade(aula, status);
    }

    if (presencaAulaRepository.findByAulaIdAndAlunoId(aula.getId(), aluno.getId()).isPresent()) {
      throw new ConflictException("Presenca already registered");
    }

    PresencaAula p = new PresencaAula();
    p.setAula(aula);
    p.setAluno(aluno);
    p.setStatus(status);
    return toResponse(presencaAulaRepository.save(p));
  }

  @Transactional
  public PresencaAulaResponse update(UserPrincipal me, Long aulaId, PresencaAulaUpdateRequest req) {
    assertProfessorOrAdmin(me);
    Aula aula = loadAndAssertInAcademia(me, aulaId);

    Aluno aluno = alunoRepository.findById(req.alunoId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
    if (!aluno.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aluno is not in your academia");
    }

    PresencaAula p = presencaAulaRepository.findByAulaIdAndAlunoId(aula.getId(), aluno.getId())
        .orElseThrow(() -> new NotFoundException("Presenca not found"));

    if (req.status() == PresencaAulaStatus.PRESENTE) {
      financeiroBloqueioService.assertPodeRegistrarPresenca(aluno);
      if (p.getStatus() != PresencaAulaStatus.PRESENTE) {
        assertCapacidade(aula, PresencaAulaStatus.PRESENTE);
      }
    }

    p.setStatus(req.status());
    return toResponse(presencaAulaRepository.save(p));
  }

  private void assertProfessorOrAdmin(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can register attendance");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private Aula loadAndAssertInAcademia(UserPrincipal me, Long aulaId) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Aula aula = aulaRepository.findById(aulaId).orElseThrow(() -> new NotFoundException("Aula not found"));
    if (!aula.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aula is not in your academia");
    }
    return aula;
  }

  private void assertCapacidade(Aula aula, PresencaAulaStatus status) {
    if (status != PresencaAulaStatus.PRESENTE) return;
    if (aula.getCapacidade() == null) return;
    long presentes = presencaAulaRepository.countByAulaIdAndStatus(aula.getId(), PresencaAulaStatus.PRESENTE);
    if (presentes >= aula.getCapacidade()) {
      throw new ConflictException("Aula is at capacity");
    }
  }

  private PresencaAulaResponse toResponse(PresencaAula p) {
    return new PresencaAulaResponse(p.getId(), p.getAluno().getId(), p.getStatus(), p.getRegistradaEm());
  }
}

