package com.fightflow.service;

import com.fightflow.dto.plano.PlanoCreateRequest;
import com.fightflow.dto.plano.PlanoResponse;
import com.fightflow.dto.plano.PlanoUpdateRequest;
import com.fightflow.entity.Plano;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.PlanoRepository;
import com.fightflow.repository.spec.PlanoSpecs;
import com.fightflow.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanoService {
  private final PlanoRepository planoRepository;
  private final AcademiaRepository academiaRepository;

  public PlanoService(PlanoRepository planoRepository, AcademiaRepository academiaRepository) {
    this.planoRepository = planoRepository;
    this.academiaRepository = academiaRepository;
  }

  @Transactional
  public PlanoResponse create(UserPrincipal me, PlanoCreateRequest req) {
    requireStaffWithAcademia(me);
    Plano p = new Plano();
    p.setAcademia(academiaRepository.getReferenceById(me.getAcademiaId()));
    p.setNome(req.nome());
    p.setDescricao(req.descricao());
    p.setValor(req.valor());
    p.setDuracaoEmDias(req.duracaoEmDias());
    p.setAtivo(req.ativo() == null || req.ativo());
    return toResponse(planoRepository.save(p));
  }

  @Transactional(readOnly = true)
  public PlanoResponse get(UserPrincipal me, Long id) {
    Plano p = planoRepository.findById(id).orElseThrow(() -> new NotFoundException("Plano not found"));
    requirePlanoAccess(me, p);
    return toResponse(p);
  }

  @Transactional(readOnly = true)
  public Page<PlanoResponse> list(UserPrincipal me, Boolean ativo, Pageable pageable) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Specification<Plano> spec = Specification.where(PlanoSpecs.academiaId(me.getAcademiaId()))
        .and(PlanoSpecs.ativo(ativo));
    return planoRepository.findAll(spec, pageable).map(this::toResponse);
  }

  @Transactional
  public PlanoResponse update(UserPrincipal me, Long id, PlanoUpdateRequest req) {
    requireStaffWithAcademia(me);
    Plano p = planoRepository.findById(id).orElseThrow(() -> new NotFoundException("Plano not found"));
    requirePlanoAccess(me, p);
    if (req.nome() != null) p.setNome(req.nome());
    if (req.descricao() != null) p.setDescricao(req.descricao());
    if (req.valor() != null) p.setValor(req.valor());
    if (req.duracaoEmDias() != null) p.setDuracaoEmDias(req.duracaoEmDias());
    if (req.ativo() != null) p.setAtivo(req.ativo());
    return toResponse(planoRepository.save(p));
  }

  @Transactional
  public void delete(UserPrincipal me, Long id) {
    requireStaffWithAcademia(me);
    Plano p = planoRepository.findById(id).orElseThrow(() -> new NotFoundException("Plano not found"));
    requirePlanoAccess(me, p);
    p.setAtivo(false);
    planoRepository.save(p);
  }

  private void requireStaffWithAcademia(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage planos");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private void requirePlanoAccess(UserPrincipal me, Plano p) {
    Long academiaId = p.getAcademia() == null ? null : p.getAcademia().getId();
    if (!me.getAcademiaId().equals(academiaId)) {
      throw new ForbiddenException("Plano does not belong to your academia");
    }
  }

  private PlanoResponse toResponse(Plano p) {
    Long academiaId = p.getAcademia() == null ? null : p.getAcademia().getId();
    return new PlanoResponse(p.getId(), academiaId, p.getNome(), p.getDescricao(), p.getValor(),
        p.getDuracaoEmDias(), p.isAtivo(), p.getCreatedAt());
  }
}

