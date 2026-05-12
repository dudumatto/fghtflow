package com.fightflow.service;

import com.fightflow.dto.academia.AcademiaRequest;
import com.fightflow.dto.academia.AcademiaResponse;
import com.fightflow.dto.academia.AcademiaResumoResponse;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcademiaService {
  private final AcademiaRepository academiaRepository;
  private final UsuarioRepository usuarioRepository;
  private final AcademiaScopeService academiaScopeService;

  public AcademiaService(
      AcademiaRepository academiaRepository,
      UsuarioRepository usuarioRepository,
      AcademiaScopeService academiaScopeService
  ) {
    this.academiaRepository = academiaRepository;
    this.usuarioRepository = usuarioRepository;
    this.academiaScopeService = academiaScopeService;
  }

  @Transactional
  public AcademiaResponse create(UserPrincipal me, AcademiaRequest req) {
    assertStaff(me);
    Usuario professorResponsavel = resolveProfessorResponsavel(me, req.professorResponsavelId());

    Academia academia = new Academia();
    academia.setNome(req.nome().trim());
    academia.setEndereco(trimToNull(req.endereco()));
    academia.setAtivo(req.ativo() == null || req.ativo());
    academia.setProfessorResponsavel(professorResponsavel);
    academia = academiaRepository.save(academia);

    if (professorResponsavel != null) {
      academiaScopeService.vincularProfessorResponsavel(professorResponsavel, academia);
    }

    return toResponse(academia);
  }

  @Transactional(readOnly = true)
  public List<AcademiaResponse> list(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return academiaRepository.findAllByOrderByNomeAsc().stream().map(this::toResponse).toList();
    }
    if (me.getRole() == Role.PROFESSOR) {
      return academiaScopeService.listarAcademiasDoProfessor(me.getId()).stream()
          .sorted(Comparator.comparing(Academia::getNome, String.CASE_INSENSITIVE_ORDER))
          .map(this::toResponse)
          .toList();
    }
    return ownAcademia(me).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public AcademiaResponse get(UserPrincipal me, Long id) {
    Academia academia = academiaRepository.findById(id).orElseThrow(() -> new NotFoundException("Academia not found"));
    assertCanView(me, academia);
    return toResponse(academia);
  }

  @Transactional
  public AcademiaResponse update(UserPrincipal me, Long id, AcademiaRequest req) {
    assertStaff(me);
    Academia academia = academiaRepository.findById(id).orElseThrow(() -> new NotFoundException("Academia not found"));
    assertCanManage(me, academia);

    academia.setNome(req.nome().trim());
    academia.setEndereco(trimToNull(req.endereco()));
    if (req.ativo() != null) {
      academia.setAtivo(req.ativo());
    }
    if (me.getRole() == Role.ADMIN && req.professorResponsavelId() != null) {
      Usuario professor = resolveProfessor(req.professorResponsavelId());
      academia.setProfessorResponsavel(professor);
      academiaScopeService.vincularProfessorResponsavel(professor, academia);
    } else if (me.getRole() == Role.PROFESSOR && academia.getProfessorResponsavel() == null) {
      Usuario professor = usuarioRepository.getReferenceById(me.getId());
      academia.setProfessorResponsavel(professor);
      academiaScopeService.vincularProfessorResponsavel(professor, academia);
    }
    return toResponse(academiaRepository.save(academia));
  }

  @Transactional
  public void softDelete(UserPrincipal me, Long id) {
    assertStaff(me);
    Academia academia = academiaRepository.findById(id).orElseThrow(() -> new NotFoundException("Academia not found"));
    assertCanManage(me, academia);
    academia.setAtivo(false);
    academiaRepository.save(academia);
  }

  @Transactional(readOnly = true)
  public List<AcademiaResumoResponse> select(UserPrincipal me) {
    if (me.getRole() == Role.ADMIN) {
      return academiaRepository.findAllByAtivoTrueOrderByNomeAsc().stream().map(this::toResumo).toList();
    }
    if (me.getRole() == Role.PROFESSOR) {
      return academiaScopeService.listarAcademiasDoProfessor(me.getId()).stream()
          .filter(Academia::isAtivo)
          .sorted(Comparator.comparing(Academia::getNome, String.CASE_INSENSITIVE_ORDER))
          .map(this::toResumo)
          .toList();
    }
    return ownAcademia(me).stream()
        .filter(Academia::isAtivo)
        .map(this::toResumo)
        .toList();
  }

  private void assertStaff(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage academias");
    }
  }

  private void assertCanView(UserPrincipal me, Academia academia) {
    if (me.getRole() == Role.ADMIN) return;
    if (me.getRole() == Role.PROFESSOR) {
      academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), academia.getId());
      return;
    }
    if (me.getAcademiaId() == null || !me.getAcademiaId().equals(academia.getId())) {
      throw new ForbiddenException("Academia does not belong to current user");
    }
  }

  private void assertCanManage(UserPrincipal me, Academia academia) {
    if (me.getRole() == Role.ADMIN) return;
    academiaScopeService.validarProfessorGerenciaAcademia(me.getId(), academia.getId());
  }

  private List<Academia> ownAcademia(UserPrincipal me) {
    if (me.getAcademiaId() == null) return List.of();
    return academiaRepository.findById(me.getAcademiaId()).map(List::of).orElseGet(List::of);
  }

  private Usuario resolveProfessorResponsavel(UserPrincipal me, Long professorResponsavelId) {
    if (me.getRole() == Role.PROFESSOR) {
      return usuarioRepository.findById(me.getId()).orElseThrow(() -> new NotFoundException("User not found"));
    }
    if (professorResponsavelId == null) return null;
    return resolveProfessor(professorResponsavelId);
  }

  private Usuario resolveProfessor(Long professorId) {
    Usuario professor = usuarioRepository.findById(professorId).orElseThrow(() -> new NotFoundException("Professor not found"));
    if (professor.getRole() != Role.PROFESSOR) {
      throw new BadRequestException("professorResponsavelId must be a PROFESSOR user");
    }
    return professor;
  }

  private String trimToNull(String value) {
    if (value == null || value.trim().isEmpty()) return null;
    return value.trim();
  }

  private AcademiaResponse toResponse(Academia academia) {
    Usuario professor = academia.getProfessorResponsavel();
    return new AcademiaResponse(
        academia.getId(),
        academia.getNome(),
        academia.getEndereco(),
        academia.isAtivo(),
        professor == null ? null : professor.getId(),
        professor == null ? null : professor.getEmail(),
        academia.getCreatedAt(),
        academia.getUpdatedAt()
    );
  }

  private AcademiaResumoResponse toResumo(Academia academia) {
    return new AcademiaResumoResponse(academia.getId(), academia.getNome());
  }
}
