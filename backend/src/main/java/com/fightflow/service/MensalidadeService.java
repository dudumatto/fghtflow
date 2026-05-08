package com.fightflow.service;

import com.fightflow.dto.mensalidade.MensalidadeCreateRequest;
import com.fightflow.dto.mensalidade.MensalidadePagamentoRequest;
import com.fightflow.dto.mensalidade.MensalidadeResponse;
import com.fightflow.dto.mensalidade.MensalidadeUpdateRequest;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.Plano;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.MensalidadeRepository;
import com.fightflow.repository.PlanoRepository;
import com.fightflow.repository.spec.MensalidadeSpecs;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensalidadeService {
  private final MensalidadeRepository mensalidadeRepository;
  private final AlunoRepository alunoRepository;
  private final PlanoRepository planoRepository;
  private final FinanceiroBloqueioService financeiroBloqueioService;

  public MensalidadeService(
      MensalidadeRepository mensalidadeRepository,
      AlunoRepository alunoRepository,
      PlanoRepository planoRepository,
      FinanceiroBloqueioService financeiroBloqueioService
  ) {
    this.mensalidadeRepository = mensalidadeRepository;
    this.alunoRepository = alunoRepository;
    this.planoRepository = planoRepository;
    this.financeiroBloqueioService = financeiroBloqueioService;
  }

  @Transactional
  public MensalidadeResponse create(UserPrincipal me, MensalidadeCreateRequest req) {
    requireStaffWithAcademia(me);
    Aluno aluno = alunoRepository.findById(req.alunoId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
    Plano plano = planoRepository.findById(req.planoId()).orElseThrow(() -> new NotFoundException("Plano not found"));
    requireAlunoInAcademia(me, aluno);
    requirePlanoInAcademia(me, plano);

    Mensalidade m = new Mensalidade();
    m.setAluno(aluno);
    m.setPlano(plano);
    m.setValor(req.valor());
    m.setVencimento(req.vencimento());
    m.setDataPagamento(req.dataPagamento());
    m.setStatus(req.status() == null ? MensalidadeStatus.PENDENTE : req.status());
    m.setMetodoPagamento(req.metodoPagamento());
    m.setReferencia(req.referencia());
    return toResponse(mensalidadeRepository.save(m));
  }

  @Transactional(readOnly = true)
  public MensalidadeResponse get(UserPrincipal me, Long id) {
    Mensalidade m = mensalidadeRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Mensalidade not found"));
    requireMensalidadeAccess(me, m);
    return toResponse(m);
  }

  @Transactional(readOnly = true)
  public Page<MensalidadeResponse> list(
      UserPrincipal me,
      Long alunoId,
      MensalidadeStatus status,
      Instant vencimentoFrom,
      Instant vencimentoTo,
      Pageable pageable
  ) {
    Specification<Mensalidade> spec;
    if (me.getRole() == Role.ATLETA) {
      spec = Specification.where(MensalidadeSpecs.usuarioId(me.getId()));
      if (alunoId != null) {
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
        requireAlunoAccess(me, aluno);
        spec = spec.and(MensalidadeSpecs.alunoId(alunoId));
      }
    } else {
      requireStaffWithAcademia(me);
      if (alunoId != null) {
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
        requireAlunoInAcademia(me, aluno);
      }
      spec = Specification.where(MensalidadeSpecs.academiaId(me.getAcademiaId()))
          .and(MensalidadeSpecs.alunoId(alunoId));
    }
    return mensalidadeRepository.findAll(spec
        .and(MensalidadeSpecs.status(status))
        .and(MensalidadeSpecs.vencimentoFrom(vencimentoFrom))
        .and(MensalidadeSpecs.vencimentoTo(vencimentoTo)), pageable).map(this::toResponse);
  }

  @Transactional
  public MensalidadeResponse update(UserPrincipal me, Long id, MensalidadeUpdateRequest req) {
    requireStaffWithAcademia(me);
    Mensalidade m = mensalidadeRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Mensalidade not found"));
    requireMensalidadeAccess(me, m);
    if (req.planoId() != null) {
      Plano plano = planoRepository.findById(req.planoId()).orElseThrow(() -> new NotFoundException("Plano not found"));
      requirePlanoInAcademia(me, plano);
      m.setPlano(plano);
    }
    if (req.valor() != null) m.setValor(req.valor());
    if (req.vencimento() != null) m.setVencimento(req.vencimento());
    if (req.dataPagamento() != null) m.setDataPagamento(req.dataPagamento());
    if (req.status() != null) m.setStatus(req.status());
    if (req.metodoPagamento() != null) m.setMetodoPagamento(req.metodoPagamento());
    if (req.referencia() != null) m.setReferencia(req.referencia());
    m = mensalidadeRepository.save(m);
    financeiroBloqueioService.regularizarAlunoSePossivel(m.getAluno());
    return toResponse(m);
  }

  @Transactional
  public MensalidadeResponse registrarPagamento(UserPrincipal me, Long id, MensalidadePagamentoRequest req) {
    requireStaffWithAcademia(me);
    Mensalidade m = mensalidadeRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Mensalidade not found"));
    requireMensalidadeAccess(me, m);
    m.setStatus(MensalidadeStatus.PAGO);
    m.setMetodoPagamento(req.metodoPagamento());
    m.setDataPagamento(req.dataPagamento() == null ? Instant.now() : req.dataPagamento());
    if (req.referencia() != null) m.setReferencia(req.referencia());
    m = mensalidadeRepository.save(m);
    financeiroBloqueioService.regularizarAlunoSePossivel(m.getAluno());
    return toResponse(m);
  }

  @Transactional
  public void cancel(UserPrincipal me, Long id) {
    requireStaffWithAcademia(me);
    Mensalidade m = mensalidadeRepository.findByIdWithAlunoAndPlano(id)
        .orElseThrow(() -> new NotFoundException("Mensalidade not found"));
    requireMensalidadeAccess(me, m);
    m.setStatus(MensalidadeStatus.CANCELADO);
    m = mensalidadeRepository.save(m);
    financeiroBloqueioService.regularizarAlunoSePossivel(m.getAluno());
  }

  private void requireStaffWithAcademia(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage mensalidades");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private void requireMensalidadeAccess(UserPrincipal me, Mensalidade m) {
    requireAlunoAccess(me, m.getAluno());
  }

  private void requireAlunoAccess(UserPrincipal me, Aluno aluno) {
    if (me.getRole() == Role.ATLETA) {
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

  private MensalidadeResponse toResponse(Mensalidade m) {
    return new MensalidadeResponse(m.getId(), m.getAluno().getId(), m.getPlano().getId(),
        m.getValor(), m.getVencimento(), m.getDataPagamento(), m.getStatus(),
        m.getMetodoPagamento(), m.getReferencia(), m.getCreatedAt());
  }
}
