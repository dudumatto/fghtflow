package com.fightflow.service;

import com.fightflow.dto.matricula.MatriculaCreateRequest;
import com.fightflow.dto.matricula.MatriculaResponse;
import com.fightflow.dto.matricula.MatriculaUpdateRequest;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Matricula;
import com.fightflow.entity.MatriculaStatus;
import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.Plano;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.MatriculaRepository;
import com.fightflow.repository.MensalidadeRepository;
import com.fightflow.repository.PlanoRepository;
import com.fightflow.repository.spec.MatriculaSpecs;
import com.fightflow.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatriculaService {
  private final MatriculaRepository matriculaRepository;
  private final AlunoRepository alunoRepository;
  private final PlanoRepository planoRepository;
  private final MensalidadeRepository mensalidadeRepository;

  public MatriculaService(
      MatriculaRepository matriculaRepository,
      AlunoRepository alunoRepository,
      PlanoRepository planoRepository,
      MensalidadeRepository mensalidadeRepository
  ) {
    this.matriculaRepository = matriculaRepository;
    this.alunoRepository = alunoRepository;
    this.planoRepository = planoRepository;
    this.mensalidadeRepository = mensalidadeRepository;
  }

  @Transactional
  public MatriculaResponse create(UserPrincipal me, MatriculaCreateRequest req) {
    requireStaffWithAcademia(me);
    Aluno aluno = alunoRepository.findById(req.alunoId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
    Plano plano = planoRepository.findById(req.planoId()).orElseThrow(() -> new NotFoundException("Plano not found"));
    requireAlunoInAcademia(me, aluno);
    requirePlanoInAcademia(me, plano);

    Matricula m = new Matricula();
    m.setAluno(aluno);
    m.setPlano(plano);
    m.setDataInicio(req.dataInicio());
    m.setDataFim(req.dataFim());
    m.setStatus(req.status() == null ? MatriculaStatus.ATIVA : req.status());
    m = matriculaRepository.save(m);
    gerarPrimeiraMensalidade(aluno, plano, m);
    return toResponse(m);
  }

  @Transactional(readOnly = true)
  public MatriculaResponse get(UserPrincipal me, Long id) {
    Matricula m = matriculaRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Matricula not found"));
    requireMatriculaAccess(me, m);
    return toResponse(m);
  }

  @Transactional(readOnly = true)
  public Page<MatriculaResponse> list(UserPrincipal me, Long alunoId, MatriculaStatus status, Pageable pageable) {
    Specification<Matricula> spec;
    if (isAlunoRole(me)) {
      spec = Specification.where(MatriculaSpecs.usuarioId(me.getId()));
      if (alunoId != null) {
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
        requireAlunoAccess(me, aluno);
        spec = spec.and(MatriculaSpecs.alunoId(alunoId));
      }
    } else {
      requireStaffWithAcademia(me);
      if (alunoId != null) {
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
        requireAlunoInAcademia(me, aluno);
      }
      spec = Specification.where(MatriculaSpecs.academiaId(me.getAcademiaId()))
          .and(MatriculaSpecs.alunoId(alunoId));
    }
    return matriculaRepository.findAll(spec.and(MatriculaSpecs.status(status)), pageable).map(this::toResponse);
  }

  @Transactional
  public MatriculaResponse update(UserPrincipal me, Long id, MatriculaUpdateRequest req) {
    requireStaffWithAcademia(me);
    Matricula m = matriculaRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Matricula not found"));
    requireMatriculaAccess(me, m);
    if (req.planoId() != null) {
      Plano plano = planoRepository.findById(req.planoId()).orElseThrow(() -> new NotFoundException("Plano not found"));
      requirePlanoInAcademia(me, plano);
      m.setPlano(plano);
    }
    if (req.dataInicio() != null) m.setDataInicio(req.dataInicio());
    if (req.dataFim() != null) m.setDataFim(req.dataFim());
    if (req.status() != null) m.setStatus(req.status());
    return toResponse(matriculaRepository.save(m));
  }

  @Transactional
  public void cancel(UserPrincipal me, Long id) {
    requireStaffWithAcademia(me);
    Matricula m = matriculaRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Matricula not found"));
    requireMatriculaAccess(me, m);
    m.setStatus(MatriculaStatus.CANCELADA);
    matriculaRepository.save(m);
  }

  private void requireStaffWithAcademia(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage matriculas");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private void requireMatriculaAccess(UserPrincipal me, Matricula m) {
    requireAlunoAccess(me, m.getAluno());
  }

  private void requireAlunoAccess(UserPrincipal me, Aluno aluno) {
    if (isAlunoRole(me)) {
      if (!me.getId().equals(aluno.getUsuario().getId())) {
        throw new ForbiddenException("Aluno does not belong to current user");
      }
      return;
    }
    requireAlunoInAcademia(me, aluno);
  }

  private void requireAlunoInAcademia(UserPrincipal me, Aluno aluno) {
    if (me.getAcademiaId() == null || !me.getAcademiaId().equals(aluno.getAcademia().getId())) {
      throw new ForbiddenException("Aluno does not belong to your academia");
    }
  }

  private void requirePlanoInAcademia(UserPrincipal me, Plano plano) {
    Long academiaId = plano.getAcademia() == null ? null : plano.getAcademia().getId();
    if (!me.getAcademiaId().equals(academiaId)) {
      throw new ForbiddenException("Plano does not belong to your academia");
    }
  }

  private boolean isAlunoRole(UserPrincipal me) {
    return me.getRole() == Role.ALUNO || me.getRole() == Role.ATLETA;
  }

  private MatriculaResponse toResponse(Matricula m) {
    return new MatriculaResponse(m.getId(), m.getAluno().getId(), m.getPlano().getId(),
        m.getDataInicio(), m.getDataFim(), m.getStatus(), m.getCreatedAt());
  }

  private void gerarPrimeiraMensalidade(Aluno aluno, Plano plano, Matricula matricula) {
    Mensalidade mensalidade = new Mensalidade();
    mensalidade.setAluno(aluno);
    mensalidade.setPlano(plano);
    mensalidade.setValor(plano.getValor());
    mensalidade.setVencimento(matricula.getDataInicio());
    mensalidade.setStatus(MensalidadeStatus.PENDENTE);
    mensalidade.setReferencia("matricula:" + matricula.getId() + ":primeira");
    mensalidadeRepository.save(mensalidade);
  }
}
