package com.fightflow.service;

import com.fightflow.dto.competicao.CompeticaoCreateRequest;
import com.fightflow.dto.competicao.CompeticaoResponse;
import com.fightflow.entity.Competicao;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.CompeticaoRepository;
import com.fightflow.repository.spec.CompeticaoSpecs;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompeticaoService {
  private final CompeticaoRepository competicaoRepository;
  private final AcademiaRepository academiaRepository;

  public CompeticaoService(CompeticaoRepository competicaoRepository, AcademiaRepository academiaRepository) {
    this.competicaoRepository = competicaoRepository;
    this.academiaRepository = academiaRepository;
  }

  @Transactional
  public CompeticaoResponse create(UserPrincipal me, CompeticaoCreateRequest req) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can create competicoes");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Competicao c = new Competicao();
    c.setAcademia(academiaRepository.getReferenceById(me.getAcademiaId()));
    c.setNome(req.nome());
    c.setLocal(req.local());
    c.setStartsAt(req.startsAt());
    return toResponse(competicaoRepository.save(c));
  }

  @Transactional(readOnly = true)
  public List<CompeticaoResponse> listForMyAcademia(UserPrincipal me) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    return competicaoRepository.findAllByAcademiaIdOrderByStartsAtDesc(me.getAcademiaId())
        .stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Page<CompeticaoResponse> list(UserPrincipal me, Instant dateFrom, Instant dateTo, Pageable pageable) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Specification<Competicao> spec = Specification.where(CompeticaoSpecs.academiaId(me.getAcademiaId()))
        .and(CompeticaoSpecs.startsAtFrom(dateFrom))
        .and(CompeticaoSpecs.startsAtTo(dateTo));
    return competicaoRepository.findAll(spec, pageable).map(this::toResponse);
  }

  private CompeticaoResponse toResponse(Competicao c) {
    return new CompeticaoResponse(c.getId(), c.getNome(), c.getLocal(), c.getStartsAt());
  }
}
