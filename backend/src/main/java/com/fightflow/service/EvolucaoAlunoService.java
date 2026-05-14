package com.fightflow.service;

import com.fightflow.dto.evolucao.EvolucaoAlunoCreateRequest;
import com.fightflow.dto.evolucao.EvolucaoAlunoResponse;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.EvolucaoAluno;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.EvolucaoAlunoRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvolucaoAlunoService {
  private final EvolucaoAlunoRepository evolucaoAlunoRepository;
  private final AlunoRepository alunoRepository;
  private final UsuarioRepository usuarioRepository;

  public EvolucaoAlunoService(EvolucaoAlunoRepository evolucaoAlunoRepository, AlunoRepository alunoRepository, UsuarioRepository usuarioRepository) {
    this.evolucaoAlunoRepository = evolucaoAlunoRepository;
    this.alunoRepository = alunoRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional
  public EvolucaoAlunoResponse create(UserPrincipal me, EvolucaoAlunoCreateRequest req) {
    assertProfessorOrAdmin(me);
    assertAcademia(me);

    Aluno aluno = alunoRepository.findById(req.alunoId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertAlunoInAcademia(me, aluno);

    EvolucaoAluno e = new EvolucaoAluno();
    e.setAluno(aluno);
    e.setTipoEnum(req.tipo());
    e.setDescricao(req.descricao());
    e.setData(req.data());
    e.setProfessorResponsavel(usuarioRepository.getReferenceById(me.getId()));
    e = evolucaoAlunoRepository.save(e);
    return toResponse(e);
  }

  @Transactional(readOnly = true)
  public List<EvolucaoAlunoResponse> list(UserPrincipal me, Long alunoId) {
    if (me.getRole() == Role.ATLETA) {
      Aluno aluno = alunoRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
          .orElseThrow(() -> new NotFoundException("Aluno not found"));
      if (alunoId != null && !aluno.getId().equals(alunoId)) {
        throw new ForbiddenException("Cannot view other aluno history");
      }
      return evolucaoAlunoRepository.findAllByAlunoIdOrderByDataDesc(aluno.getId()).stream().map(this::toResponse).toList();
    }

    assertProfessorOrAdmin(me);
    assertAcademia(me);
    if (alunoId == null) {
      throw new BadRequestException("alunoId is required");
    }
    Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertAlunoInAcademia(me, aluno);
    return evolucaoAlunoRepository.findAllByAlunoIdOrderByDataDesc(alunoId).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public long countForAluno(UserPrincipal me, Long alunoId) {
    if (alunoId == null) return 0;
    if (me.getRole() == Role.ATLETA) {
      Aluno aluno = alunoRepository.findByUsuarioId(me.getId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
      if (!aluno.getId().equals(alunoId)) throw new ForbiddenException("Forbidden");
      return evolucaoAlunoRepository.countByAlunoId(alunoId);
    }
    assertAcademia(me);
    Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertAlunoInAcademia(me, aluno);
    return evolucaoAlunoRepository.countByAlunoId(alunoId);
  }

  private void assertProfessorOrAdmin(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage evolucoes");
    }
  }

  private void assertAcademia(UserPrincipal me) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private void assertAlunoInAcademia(UserPrincipal me, Aluno aluno) {
    if (aluno.getAcademia() == null || aluno.getAcademia().getId() == null || !aluno.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aluno is not in your academia");
    }
  }

  private EvolucaoAlunoResponse toResponse(EvolucaoAluno e) {
    return new EvolucaoAlunoResponse(
        e.getId(),
        e.getAluno().getId(),
        e.getTipoEnum(),
        e.getDescricao(),
        e.getData(),
        e.getProfessorResponsavel().getId()
    );
  }
}
