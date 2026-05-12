package com.fightflow.service;

import com.fightflow.dto.common.SelectOptionResponse;
import com.fightflow.dto.professor.ProfessorRequest;
import com.fightflow.dto.professor.ProfessorResponse;
import com.fightflow.entity.Academia;
import com.fightflow.entity.ProfessorAcademia;
import com.fightflow.entity.ProfessorAcademiaPapel;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ConflictException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.ProfessorAcademiaRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorService {
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final ProfessorAcademiaRepository professorAcademiaRepository;
  private final AcademiaScopeService academiaScopeService;
  private final PasswordEncoder passwordEncoder;

  public ProfessorService(
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      ProfessorAcademiaRepository professorAcademiaRepository,
      AcademiaScopeService academiaScopeService,
      PasswordEncoder passwordEncoder
  ) {
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.professorAcademiaRepository = professorAcademiaRepository;
    this.academiaScopeService = academiaScopeService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public ProfessorResponse create(UserPrincipal me, ProfessorRequest req) {
    if (me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only ADMIN can create professores");
    }
    if (req.email() == null || req.email().isBlank()) throw new BadRequestException("email is required");
    if (req.password() == null || req.password().isBlank()) throw new BadRequestException("password is required");
    if (req.academiaId() == null) throw new BadRequestException("academiaId is required");
    if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
      throw new ConflictException("Email already in use");
    }

    Academia academia = academiaRepository.findById(req.academiaId())
        .orElseThrow(() -> new NotFoundException("Academia not found"));
    if (!academia.isAtivo()) throw new BadRequestException("Academia is inactive");

    Usuario professor = new Usuario();
    professor.setEmail(req.email().trim().toLowerCase());
    professor.setPasswordHash(passwordEncoder.encode(req.password()));
    professor.setRole(Role.PROFESSOR);
    professor.setAcademia(academia);
    professor = usuarioRepository.save(professor);

    academiaScopeService.vincularProfessorAcademia(professor, academia, ProfessorAcademiaPapel.RESPONSAVEL);
    if (academia.getProfessorResponsavel() == null) {
      academia.setProfessorResponsavel(professor);
      academiaRepository.save(academia);
    }
    return toResponse(professor);
  }

  @Transactional(readOnly = true)
  public List<SelectOptionResponse> select(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return usuarioRepository.findAllByRoleOrderByEmailAsc(Role.PROFESSOR).stream()
          .map(this::toSelect)
          .toList();
    }
    if (me.getRole() != Role.PROFESSOR) {
      return List.of();
    }

    LinkedHashMap<Long, Usuario> professores = new LinkedHashMap<>();
    usuarioRepository.findById(me.getId()).ifPresent(self -> professores.put(self.getId(), self));
    for (Academia academia : academiaScopeService.listarAcademiasDoProfessor(me.getId())) {
      for (ProfessorAcademia vinculo : professorAcademiaRepository.findAllByAcademiaIdAndAtivoTrue(academia.getId())) {
        professores.put(vinculo.getProfessor().getId(), vinculo.getProfessor());
      }
    }
    return professores.values().stream().map(this::toSelect).toList();
  }

  private ProfessorResponse toResponse(Usuario professor) {
    Academia academia = professor.getAcademia();
    return new ProfessorResponse(
        professor.getId(),
        displayName(professor),
        professor.getEmail(),
        professor.getRole(),
        academia == null ? null : academia.getId(),
        academia == null ? null : academia.getNome(),
        professor.getCreatedAt()
    );
  }

  private SelectOptionResponse toSelect(Usuario professor) {
    return new SelectOptionResponse(professor.getId(), displayName(professor));
  }

  private String displayName(Usuario professor) {
    String email = professor.getEmail();
    int at = email == null ? -1 : email.indexOf('@');
    return at > 0 ? email.substring(0, at) : email;
  }
}
