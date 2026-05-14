package com.fightflow.service;

import com.fightflow.dto.aluno.AlunoRequest;
import com.fightflow.dto.aluno.AlunoResponse;
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
public class AlunoService {
  private final AlunoRepository alunoRepository;
  private final AtletaRepository atletaRepository;
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final AcademiaScopeService academiaScopeService;
  private final PasswordEncoder passwordEncoder;

  public AlunoService(
      AlunoRepository alunoRepository,
      AtletaRepository atletaRepository,
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      AcademiaScopeService academiaScopeService,
      PasswordEncoder passwordEncoder
  ) {
    this.alunoRepository = alunoRepository;
    this.atletaRepository = atletaRepository;
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.academiaScopeService = academiaScopeService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public AlunoResponse create(UserPrincipal me, AlunoRequest req) {
    requireStaff(me);
    requireCreateFields(req);
    Academia academia = resolveAcademiaForWrite(me, req.academiaId());
    if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
      throw new ConflictException("Email already in use");
    }

    Usuario usuario = new Usuario();
    usuario.setEmail(req.email().trim().toLowerCase());
    usuario.setPasswordHash(passwordEncoder.encode(req.password()));
    usuario.setRole(Role.ALUNO);
    usuario.setAcademia(academia);
    usuario = usuarioRepository.save(usuario);

    Aluno aluno = new Aluno();
    aluno.setUsuario(usuario);
    aluno.setAcademia(academia);
    aluno.setNome(trimToDefault(req.nome(), defaultName(usuario.getEmail())));
    aluno.setAtivo(req.ativo() == null || req.ativo());
    aluno.setFaixaAtual(trimToNull(req.faixaAtual()));
    aluno.setGrauAtual(req.grauAtual() == null ? 0 : req.grauAtual());
    return toResponse(alunoRepository.save(aluno));
  }

  @Transactional(readOnly = true)
  public List<AlunoResponse> list(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return alunoRepository.findAllByOrderByNomeAsc().stream().map(this::toResponse).toList();
    }
    if (me.getRole() == Role.PROFESSOR) {
      List<Long> ids = managedAcademiaIds(me);
      if (ids.isEmpty()) return List.of();
      return alunoRepository.findAllByAcademiaIdInOrderByNomeAsc(ids).stream().map(this::toResponse).toList();
    }
    Aluno aluno = alunoRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Aluno not found"));
    return List.of(toResponse(aluno));
  }

  @Transactional(readOnly = true)
  public AlunoResponse get(UserPrincipal me, Long id) {
    Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertCanAccess(me, aluno);
    return toResponse(aluno);
  }

  @Transactional
  public AlunoResponse update(UserPrincipal me, Long id, AlunoRequest req) {
    requireStaff(me);
    Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertCanManage(me, aluno);

    if (req.email() != null && !req.email().isBlank()) {
      String email = req.email().trim().toLowerCase();
      usuarioRepository.findByEmailIgnoreCase(email)
          .filter(existing -> !existing.getId().equals(aluno.getUsuario().getId()))
          .ifPresent(existing -> { throw new ConflictException("Email already in use"); });
      aluno.getUsuario().setEmail(email);
    }
    if (req.password() != null && !req.password().isBlank()) {
      aluno.getUsuario().setPasswordHash(passwordEncoder.encode(req.password()));
    }
    if (req.academiaId() != null) {
      Academia academia = resolveAcademiaForWrite(me, req.academiaId());
      aluno.setAcademia(academia);
      aluno.getUsuario().setAcademia(academia);
      atletaRepository.findByAlunoId(aluno.getId()).ifPresent(a -> a.setAcademia(academia));
    }
    if (req.nome() != null) aluno.setNome(trimToDefault(req.nome(), aluno.getNome()));
    if (req.ativo() != null) aluno.setAtivo(req.ativo());
    if (req.faixaAtual() != null) aluno.setFaixaAtual(trimToNull(req.faixaAtual()));
    if (req.grauAtual() != null) aluno.setGrauAtual(req.grauAtual());

    return toResponse(alunoRepository.save(aluno));
  }

  @Transactional
  public void softDelete(UserPrincipal me, Long id) {
    requireStaff(me);
    Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertCanManage(me, aluno);
    aluno.setAtivo(false);
    atletaRepository.findByAlunoId(aluno.getId()).ifPresent(a -> a.setAtivo(false));
    alunoRepository.save(aluno);
  }

  @Transactional(readOnly = true)
  public List<SelectOptionResponse> select(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return alunoRepository.findAllByAtivoTrueOrderByNomeAsc().stream().map(this::toSelect).toList();
    }
    if (me.getRole() == Role.PROFESSOR) {
      List<Long> ids = managedAcademiaIds(me);
      if (ids.isEmpty()) return List.of();
      return alunoRepository.findAllByAcademiaIdInAndAtivoTrueOrderByNomeAsc(ids).stream().map(this::toSelect).toList();
    }
    Aluno aluno = alunoRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Aluno not found"));
    return aluno.isAtivo() ? List.of(toSelect(aluno)) : List.of();
  }

  private void requireStaff(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage alunos");
    }
  }

  private void requireCreateFields(AlunoRequest req) {
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

  private void assertCanAccess(UserPrincipal me, Aluno aluno) {
    if (me.getRole() == Role.ADMIN) return;
    if (me.getRole() == Role.PROFESSOR) {
      academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), aluno.getAcademia().getId());
      return;
    }
    if (!me.getId().equals(aluno.getUsuario().getId())) {
      throw new ForbiddenException("Cannot access other aluno");
    }
  }

  private void assertCanManage(UserPrincipal me, Aluno aluno) {
    if (me.getRole() == Role.ADMIN) return;
    academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), aluno.getAcademia().getId());
  }

  private List<Long> managedAcademiaIds(UserPrincipal me) {
    return academiaScopeService.listarAcademiasDoProfessor(me.getId()).stream().map(Academia::getId).toList();
  }

  private AlunoResponse toResponse(Aluno aluno) {
    return new AlunoResponse(
        aluno.getId(),
        aluno.getUsuario().getId(),
        aluno.getNome(),
        aluno.getUsuario().getEmail(),
        aluno.getUsuario().getRole(),
        aluno.getAcademia().getId(),
        aluno.getAcademia().getNome(),
        aluno.isAtivo(),
        aluno.getFaixaAtual(),
        aluno.getGrauAtual(),
        aluno.getCreatedAt()
    );
  }

  private SelectOptionResponse toSelect(Aluno aluno) {
    return new SelectOptionResponse(aluno.getId(), aluno.getNome() == null ? aluno.getUsuario().getEmail() : aluno.getNome());
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
