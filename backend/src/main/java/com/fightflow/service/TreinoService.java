package com.fightflow.service;

import com.fightflow.dto.treino.TreinoCreateRequest;
import com.fightflow.dto.treino.TreinoResponse;
import com.fightflow.entity.Role;
import com.fightflow.entity.Treino;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.TreinoRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.repository.spec.TreinoSpecs;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TreinoService {
  private final TreinoRepository treinoRepository;
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;

  public TreinoService(TreinoRepository treinoRepository, UsuarioRepository usuarioRepository, AcademiaRepository academiaRepository) {
    this.treinoRepository = treinoRepository;
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
  }

  @Transactional
  public TreinoResponse create(UserPrincipal me, TreinoCreateRequest req) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can create treinos");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Treino t = new Treino();
    t.setAcademia(academiaRepository.getReferenceById(me.getAcademiaId()));
    t.setProfessor(usuarioRepository.getReferenceById(me.getId()));
    t.setStartsAt(req.startsAt());
    t.setTitulo(req.titulo());
    t.setDescricao(req.descricao());
    return toResponse(treinoRepository.save(t));
  }

  @Transactional(readOnly = true)
  public List<TreinoResponse> listForMyAcademia(UserPrincipal me) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    return treinoRepository.findAllByAcademiaIdOrderByStartsAtDesc(me.getAcademiaId())
        .stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Page<TreinoResponse> list(UserPrincipal me, Instant dateFrom, Instant dateTo, Pageable pageable) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Specification<Treino> spec = Specification.where(TreinoSpecs.academiaId(me.getAcademiaId()))
        .and(TreinoSpecs.startsAtFrom(dateFrom))
        .and(TreinoSpecs.startsAtTo(dateTo));
    return treinoRepository.findAll(spec, pageable).map(this::toResponse);
  }

  private TreinoResponse toResponse(Treino t) {
    return new TreinoResponse(t.getId(), t.getStartsAt(), t.getTitulo(), t.getDescricao());
  }
}
