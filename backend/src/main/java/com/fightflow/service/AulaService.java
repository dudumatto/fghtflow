package com.fightflow.service;

import com.fightflow.dto.aula.AulaCreateRequest;
import com.fightflow.dto.aula.AulaResponse;
import com.fightflow.dto.aula.AulaUpdateRequest;
import com.fightflow.entity.Aula;
import com.fightflow.entity.AulaTipo;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.AulaRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.repository.spec.AulaSpecs;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AulaService {
  private final AulaRepository aulaRepository;
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;

  public AulaService(AulaRepository aulaRepository, UsuarioRepository usuarioRepository, AcademiaRepository academiaRepository) {
    this.aulaRepository = aulaRepository;
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
  }

  @Transactional
  public AulaResponse create(UserPrincipal me, AulaCreateRequest req) {
    assertProfessorOrAdmin(me, "create aulas");
    assertAcademia(me);
    assertHorario(req.dataHoraInicio(), req.dataHoraFim());

    Aula a = new Aula();
    a.setAcademia(academiaRepository.getReferenceById(me.getAcademiaId()));
    a.setProfessor(usuarioRepository.getReferenceById(me.getId()));
    a.setTitulo(req.titulo());
    a.setDescricao(req.descricao());
    a.setTipo(req.tipo());
    a.setDataHoraInicio(req.dataHoraInicio());
    a.setDataHoraFim(req.dataHoraFim());
    a.setCapacidade(req.capacidade());
    if (req.tipo() == AulaTipo.PARTICULAR && a.getCapacidade() == null) {
      a.setCapacidade(1);
    }
    return toResponse(aulaRepository.save(a));
  }

  public AulaResponse get(UserPrincipal me, Long id) {
    assertAcademia(me);
    Aula a = aulaRepository.findById(id).orElseThrow(() -> new NotFoundException("Aula not found"));
    assertInAcademia(me, a);
    return toResponse(a);
  }

  public Page<AulaResponse> list(
      UserPrincipal me,
      Instant dateFrom,
      Instant dateTo,
      com.fightflow.entity.AulaTipo tipo,
      Boolean ativa,
      Long professorUsuarioId,
      String q,
      Pageable pageable
  ) {
    assertAcademia(me);
    Specification<Aula> spec = Specification.where(AulaSpecs.academiaId(me.getAcademiaId()))
        .and(AulaSpecs.inicioFrom(dateFrom))
        .and(AulaSpecs.inicioTo(dateTo))
        .and(AulaSpecs.tipo(tipo))
        .and(AulaSpecs.ativa(ativa))
        .and(AulaSpecs.professorUsuarioId(professorUsuarioId))
        .and(AulaSpecs.tituloLike(q));
    return aulaRepository.findAll(spec, pageable).map(this::toResponse);
  }

  @Transactional
  public AulaResponse update(UserPrincipal me, Long id, AulaUpdateRequest req) {
    assertProfessorOrAdmin(me, "update aulas");
    assertAcademia(me);

    Aula a = aulaRepository.findById(id).orElseThrow(() -> new NotFoundException("Aula not found"));
    assertInAcademia(me, a);
    assertCanManage(me, a);

    Instant inicio = req.dataHoraInicio() != null ? req.dataHoraInicio() : a.getDataHoraInicio();
    Instant fim = req.dataHoraFim() != null ? req.dataHoraFim() : a.getDataHoraFim();
    assertHorario(inicio, fim);

    if (req.titulo() != null) a.setTitulo(req.titulo());
    if (req.descricao() != null) a.setDescricao(req.descricao());
    if (req.tipo() != null) a.setTipo(req.tipo());
    if (req.dataHoraInicio() != null) a.setDataHoraInicio(req.dataHoraInicio());
    if (req.dataHoraFim() != null) a.setDataHoraFim(req.dataHoraFim());
    if (req.capacidade() != null) a.setCapacidade(req.capacidade());
    if (req.ativa() != null) a.setAtiva(req.ativa());

    if (a.getTipo() == AulaTipo.PARTICULAR && a.getCapacidade() == null) {
      a.setCapacidade(1);
    }
    return toResponse(aulaRepository.save(a));
  }

  @Transactional
  public void delete(UserPrincipal me, Long id) {
    assertProfessorOrAdmin(me, "delete aulas");
    assertAcademia(me);

    Aula a = aulaRepository.findById(id).orElseThrow(() -> new NotFoundException("Aula not found"));
    assertInAcademia(me, a);
    assertCanManage(me, a);
    a.setAtiva(false);
    aulaRepository.save(a);
  }

  private void assertProfessorOrAdmin(UserPrincipal me, String action) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can " + action);
    }
  }

  private void assertAcademia(UserPrincipal me) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private void assertInAcademia(UserPrincipal me, Aula aula) {
    if (!aula.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aula is not in your academia");
    }
  }

  private void assertCanManage(UserPrincipal me, Aula aula) {
    if (me.getRole() == Role.ADMIN) return;
    if (!aula.getProfessor().getId().equals(me.getId())) {
      throw new ForbiddenException("Only the aula professor can manage it");
    }
  }

  private void assertHorario(Instant inicio, Instant fim) {
    if (inicio == null || fim == null) {
      throw new BadRequestException("dataHoraInicio and dataHoraFim are required");
    }
    if (!fim.isAfter(inicio)) {
      throw new BadRequestException("dataHoraFim must be after dataHoraInicio");
    }
  }

  private AulaResponse toResponse(Aula a) {
    return new AulaResponse(
        a.getId(),
        a.getProfessor().getId(),
        a.getTitulo(),
        a.getDescricao(),
        a.getTipo(),
        a.getDataHoraInicio(),
        a.getDataHoraFim(),
        a.getCapacidade(),
        a.isAtiva()
    );
  }
}
