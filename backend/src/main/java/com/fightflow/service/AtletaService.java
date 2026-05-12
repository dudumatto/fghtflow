package com.fightflow.service;

import com.fightflow.dto.atleta.AtletaProfileResponse;
import com.fightflow.dto.atleta.AtletaRequest;
import com.fightflow.dto.atleta.AtletaResponse;
import com.fightflow.dto.atleta.AtletaUpdateRequest;
import com.fightflow.dto.common.SelectOptionResponse;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ConflictException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtletaService {
  private final AtletaRepository atletaRepository;
  private final AlunoRepository alunoRepository;
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final AcademiaScopeService academiaScopeService;
  private final FinanceiroBloqueioService financeiroBloqueioService;
  private final PasswordEncoder passwordEncoder;

  public AtletaService(
      AtletaRepository atletaRepository,
      AlunoRepository alunoRepository,
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      AcademiaScopeService academiaScopeService,
      FinanceiroBloqueioService financeiroBloqueioService,
      PasswordEncoder passwordEncoder
  ) {
    this.atletaRepository = atletaRepository;
    this.alunoRepository = alunoRepository;
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.academiaScopeService = academiaScopeService;
    this.financeiroBloqueioService = financeiroBloqueioService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public AtletaResponse create(UserPrincipal me, AtletaRequest req) {
    requireStaff(me);
    requireCreateFields(req);
    Academia academia = resolveAcademiaForWrite(me, req.academiaId());
    if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
      throw new ConflictException("Email already in use");
    }

    Usuario usuario = new Usuario();
    usuario.setEmail(req.email().trim().toLowerCase());
    usuario.setPasswordHash(passwordEncoder.encode(req.password()));
    usuario.setRole(Role.ATLETA);
    usuario.setAcademia(academia);
    usuario = usuarioRepository.save(usuario);

    Aluno aluno = new Aluno();
    aluno.setUsuario(usuario);
    aluno.setAcademia(academia);
    aluno.setNome(trimToDefault(req.nome(), defaultName(usuario.getEmail())));
    aluno.setAtivo(req.ativo() == null || req.ativo());
    aluno.setFaixaAtual(trimToNull(req.faixa()));
    aluno.setGrauAtual(req.grauAtual() == null ? 0 : req.grauAtual());
    aluno = alunoRepository.save(aluno);

    Atleta atleta = new Atleta();
    atleta.setUsuario(usuario);
    atleta.setAcademia(academia);
    atleta.setAluno(aluno);
    atleta.setFaixa(trimToNull(req.faixa()));
    atleta.setPeso(req.peso());
    atleta.setCategoria(trimToNull(req.categoria()));
    atleta.setAtivo(req.ativo() == null || req.ativo());
    return toResponse(atletaRepository.save(atleta));
  }

  @Transactional(readOnly = true)
  public List<AtletaResponse> list(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return atletaRepository.findAllByOrderByUsuarioEmailAsc().stream().map(this::toResponse).toList();
    }
    if (me.getRole() == Role.PROFESSOR) {
      List<Long> ids = managedAcademiaIds(me);
      if (ids.isEmpty()) return List.of();
      return atletaRepository.findAllByAcademiaIdInOrderByUsuarioEmailAsc(ids).stream().map(this::toResponse).toList();
    }
    Atleta atleta = atletaRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    return List.of(toResponse(atleta));
  }

  @Transactional(readOnly = true)
  public AtletaResponse get(UserPrincipal me, Long id) {
    Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new NotFoundException("Atleta not found"));
    assertCanAccess(me, atleta);
    return toResponse(atleta);
  }

  @Transactional
  public AtletaResponse update(UserPrincipal me, Long id, AtletaRequest req) {
    requireStaff(me);
    Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new NotFoundException("Atleta not found"));
    assertCanManage(me, atleta);

    if (req.email() != null && !req.email().isBlank()) {
      String email = req.email().trim().toLowerCase();
      usuarioRepository.findByEmailIgnoreCase(email)
          .filter(existing -> !existing.getId().equals(atleta.getUsuario().getId()))
          .ifPresent(existing -> { throw new ConflictException("Email already in use"); });
      atleta.getUsuario().setEmail(email);
    }
    if (req.password() != null && !req.password().isBlank()) {
      atleta.getUsuario().setPasswordHash(passwordEncoder.encode(req.password()));
    }
    if (req.academiaId() != null) {
      Academia academia = resolveAcademiaForWrite(me, req.academiaId());
      atleta.setAcademia(academia);
      atleta.getUsuario().setAcademia(academia);
      if (atleta.getAluno() != null) atleta.getAluno().setAcademia(academia);
    }
    if (req.nome() != null && atleta.getAluno() != null) {
      atleta.getAluno().setNome(trimToDefault(req.nome(), atleta.getAluno().getNome()));
    }
    if (req.ativo() != null) {
      atleta.setAtivo(req.ativo());
      if (atleta.getAluno() != null) atleta.getAluno().setAtivo(req.ativo());
    }
    if (req.faixa() != null) {
      atleta.setFaixa(trimToNull(req.faixa()));
      if (atleta.getAluno() != null) atleta.getAluno().setFaixaAtual(trimToNull(req.faixa()));
    }
    if (req.grauAtual() != null && atleta.getAluno() != null) {
      atleta.getAluno().setGrauAtual(req.grauAtual());
    }
    if (req.peso() != null) atleta.setPeso(req.peso());
    if (req.categoria() != null) atleta.setCategoria(trimToNull(req.categoria()));
    return toResponse(atletaRepository.save(atleta));
  }

  @Transactional
  public void softDelete(UserPrincipal me, Long id) {
    requireStaff(me);
    Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new NotFoundException("Atleta not found"));
    assertCanManage(me, atleta);
    atleta.setAtivo(false);
    if (atleta.getAluno() != null) atleta.getAluno().setAtivo(false);
    atletaRepository.save(atleta);
  }

  @Transactional(readOnly = true)
  public List<SelectOptionResponse> select(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return atletaRepository.findAllByAtivoTrueOrderByUsuarioEmailAsc().stream().map(this::toSelect).toList();
    }
    if (me.getRole() == Role.PROFESSOR) {
      List<Long> ids = managedAcademiaIds(me);
      if (ids.isEmpty()) return List.of();
      return atletaRepository.findAllByAcademiaIdInAndAtivoTrueOrderByUsuarioEmailAsc(ids).stream().map(this::toSelect).toList();
    }
    Atleta atleta = atletaRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    return atleta.isAtivo() ? List.of(toSelect(atleta)) : List.of();
  }

  @Transactional(readOnly = true)
  public AtletaProfileResponse getMe(UserPrincipal me) {
    requireAtleta(me);
    Atleta a = atletaRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    return toProfile(a);
  }

  @Transactional
  public AtletaProfileResponse updateMe(UserPrincipal me, AtletaUpdateRequest req) {
    requireAtleta(me);
    Atleta a = atletaRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    if (req.faixa() != null) a.setFaixa(req.faixa());
    if (req.peso() != null) a.setPeso(req.peso());
    if (req.categoria() != null) a.setCategoria(req.categoria());
    return toProfile(atletaRepository.save(a));
  }

  private void requireAtleta(UserPrincipal me) {
    if (me.getRole() != Role.ATLETA && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only ATLETA can access this resource");
    }
  }

  private void requireStaff(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage atletas");
    }
  }

  private void requireCreateFields(AtletaRequest req) {
    if (req.email() == null || req.email().isBlank()) throw new BadRequestException("email is required");
    if (req.password() == null || req.password().isBlank()) throw new BadRequestException("password is required");
    if (req.academiaId() == null) throw new BadRequestException("academiaId is required");
  }

  private Academia resolveAcademiaForWrite(UserPrincipal me, Long academiaId) {
    Academia academia = academiaRepository.findById(academiaId).orElseThrow(() -> new NotFoundException("Academia not found"));
    if (!academia.isAtivo()) throw new BadRequestException("Academia is inactive");
    if (me.getRole() == Role.PROFESSOR) academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), academiaId);
    return academia;
  }

  private void assertCanAccess(UserPrincipal me, Atleta atleta) {
    if (me.getRole() == Role.ADMIN) return;
    if (me.getRole() == Role.PROFESSOR) {
      academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), atleta.getAcademia().getId());
      return;
    }
    if (!me.getId().equals(atleta.getUsuario().getId())) {
      throw new ForbiddenException("Cannot access other atleta");
    }
  }

  private void assertCanManage(UserPrincipal me, Atleta atleta) {
    if (me.getRole() == Role.ADMIN) return;
    academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), atleta.getAcademia().getId());
  }

  private List<Long> managedAcademiaIds(UserPrincipal me) {
    return academiaScopeService.listarAcademiasDoProfessor(me.getId()).stream().map(Academia::getId).toList();
  }

  private AtletaResponse toResponse(Atleta atleta) {
    Aluno aluno = atleta.getAluno();
    return new AtletaResponse(
        atleta.getId(),
        atleta.getUsuario().getId(),
        aluno == null ? null : aluno.getId(),
        aluno == null ? defaultName(atleta.getUsuario().getEmail()) : aluno.getNome(),
        atleta.getUsuario().getEmail(),
        atleta.getUsuario().getRole(),
        atleta.getAcademia().getId(),
        atleta.getAcademia().getNome(),
        atleta.isAtivo(),
        atleta.getFaixa(),
        aluno == null ? 0 : aluno.getGrauAtual(),
        atleta.getPeso(),
        atleta.getCategoria(),
        atleta.getCreatedAt()
    );
  }

  private SelectOptionResponse toSelect(Atleta atleta) {
    Aluno aluno = atleta.getAluno();
    String nome = aluno == null || aluno.getNome() == null ? atleta.getUsuario().getEmail() : aluno.getNome();
    return new SelectOptionResponse(atleta.getId(), nome);
  }

  private AtletaProfileResponse toProfile(Atleta a) {
    Long academiaId = a.getAcademia() == null ? null : a.getAcademia().getId();
    return new AtletaProfileResponse(
        a.getId(),
        a.getUsuario().getId(),
        academiaId,
        a.getUsuario().getEmail(),
        a.getFaixa(),
        a.getPeso(),
        a.getCategoria(),
        financeiroBloqueioService.status(a.getAluno())
    );
  }

  private String defaultName(String email) {
    int at = email == null ? -1 : email.indexOf('@');
    return at > 0 ? email.substring(0, at) : email;
  }

  private String trimToDefault(String value, String fallback) {
    String trimmed = trimToNull(value);
    return trimmed == null ? fallback : trimmed;
  }

  private String trimToNull(String value) {
    if (value == null || value.trim().isEmpty()) return null;
    return value.trim();
  }
}
