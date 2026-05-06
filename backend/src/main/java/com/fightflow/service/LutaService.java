package com.fightflow.service;

import com.fightflow.dto.luta.LutaCreateRequest;
import com.fightflow.dto.luta.LutaResponse;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Competicao;
import com.fightflow.entity.Luta;
import com.fightflow.entity.LutaResultado;
import com.fightflow.entity.Role;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.CompeticaoRepository;
import com.fightflow.repository.LutaRepository;
import com.fightflow.repository.spec.LutaSpecs;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class LutaService {
  private final LutaRepository lutaRepository;
  private final AtletaRepository atletaRepository;
  private final CompeticaoRepository competicaoRepository;

  public LutaService(LutaRepository lutaRepository, AtletaRepository atletaRepository, CompeticaoRepository competicaoRepository) {
    this.lutaRepository = lutaRepository;
    this.atletaRepository = atletaRepository;
    this.competicaoRepository = competicaoRepository;
  }

  public LutaResponse create(UserPrincipal me, LutaCreateRequest req) {
    Atleta atleta = atletaRepository.findById(req.atletaId()).orElseThrow(() -> new NotFoundException("Atleta not found"));
    assertCanAccessAtleta(me, atleta);

    Competicao comp = null;
    if (req.competicaoId() != null) {
      comp = competicaoRepository.findById(req.competicaoId()).orElseThrow(() -> new NotFoundException("Competicao not found"));
      // PROFESSOR must not associate fights to a competition outside the academy.
      if (me.getRole() == Role.PROFESSOR && me.getAcademiaId() != null) {
        if (!comp.getAcademia().getId().equals(me.getAcademiaId())) {
          throw new ForbiddenException("Competicao is not in your academia");
        }
      }
    }

    Luta l = new Luta();
    l.setAtleta(atleta);
    l.setCompeticao(comp);
    l.setAdversarioNome(req.adversarioNome());
    l.setResultado(req.resultado());
    l.setMetodo(req.metodo());
    l.setFoughtAt(req.foughtAt());
    return toResponse(lutaRepository.save(l));
  }

  public List<LutaResponse> listByAtleta(UserPrincipal me, Long atletaId) {
    Atleta atleta = atletaRepository.findById(atletaId).orElseThrow(() -> new NotFoundException("Atleta not found"));
    assertCanAccessAtleta(me, atleta);
    return lutaRepository.findAllByAtletaIdOrderByFoughtAtDesc(atletaId).stream().map(this::toResponse).toList();
  }

  public Page<LutaResponse> list(UserPrincipal me, Long atletaId, LutaResultado resultado, Instant dateFrom, Instant dateTo, Pageable pageable) {
    Specification<Luta> spec = Specification.where(LutaSpecs.resultado(resultado))
        .and(LutaSpecs.foughtAtFrom(dateFrom))
        .and(LutaSpecs.foughtAtTo(dateTo));

    if (me.getRole() == Role.ATLETA) {
      Atleta self = atletaRepository.findByUsuarioId(me.getId()).orElseThrow(() -> new NotFoundException("Atleta not found"));
      if (atletaId != null && !self.getId().equals(atletaId)) {
        throw new ForbiddenException("Cannot access other athlete");
      }
      spec = spec.and(LutaSpecs.atletaId(self.getId()));
    } else if (me.getRole() == Role.PROFESSOR) {
      if (me.getAcademiaId() == null) throw new ForbiddenException("Academia scope required");
      spec = spec.and(LutaSpecs.academiaId(me.getAcademiaId()));
      if (atletaId != null) {
        Atleta atleta = atletaRepository.findById(atletaId).orElseThrow(() -> new NotFoundException("Atleta not found"));
        if (!atleta.getAcademia().getId().equals(me.getAcademiaId())) {
          throw new ForbiddenException("Athlete not in your academia");
        }
        spec = spec.and(LutaSpecs.atletaId(atletaId));
      }
    } else if (me.getRole() == Role.ADMIN) {
      spec = spec.and(LutaSpecs.atletaId(atletaId));
    } else {
      throw new ForbiddenException("Forbidden");
    }

    return lutaRepository.findAll(spec, pageable).map(this::toResponse);
  }

  private void assertCanAccessAtleta(UserPrincipal me, Atleta atleta) {
    if (me.getRole() == Role.ADMIN) return;
    if (me.getRole() == Role.ATLETA) {
      if (!atleta.getUsuario().getId().equals(me.getId())) {
        throw new ForbiddenException("Cannot access other athlete");
      }
      return;
    }
    if (me.getRole() == Role.PROFESSOR) {
      if (me.getAcademiaId() == null || atleta.getAcademia() == null) {
        throw new ForbiddenException("Academia scope required");
      }
      if (!atleta.getAcademia().getId().equals(me.getAcademiaId())) {
        throw new ForbiddenException("Athlete not in your academia");
      }
      return;
    }
    throw new ForbiddenException("Forbidden");
  }

  private LutaResponse toResponse(Luta l) {
    Long compId = l.getCompeticao() == null ? null : l.getCompeticao().getId();
    return new LutaResponse(l.getId(), l.getAtleta().getId(), compId, l.getAdversarioNome(), l.getResultado(), l.getMetodo(),
        l.getFoughtAt());
  }
}
