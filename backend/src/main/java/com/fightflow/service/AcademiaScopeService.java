package com.fightflow.service;

import com.fightflow.entity.Academia;
import com.fightflow.entity.ProfessorAcademia;
import com.fightflow.entity.ProfessorAcademiaPapel;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.ProfessorAcademiaRepository;
import com.fightflow.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcademiaScopeService {
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final ProfessorAcademiaRepository professorAcademiaRepository;

  public AcademiaScopeService(
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      ProfessorAcademiaRepository professorAcademiaRepository
  ) {
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.professorAcademiaRepository = professorAcademiaRepository;
  }

  @Transactional(readOnly = true)
  public boolean professorGerenciaAcademia(Long usuarioId, Long academiaId) {
    Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new NotFoundException("User not found"));
    if (usuario.getRole() == Role.ADMIN) return true;
    if (usuario.getRole() != Role.PROFESSOR) return false;
    if (professorAcademiaRepository.existsByProfessorIdAndAcademiaIdAndAtivoTrue(usuarioId, academiaId)) return true;
    return usuario.getAcademia() != null && academiaId.equals(usuario.getAcademia().getId());
  }

  @Transactional(readOnly = true)
  public List<Academia> listarAcademiasDoProfessor(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new NotFoundException("User not found"));
    if (usuario.getRole() == Role.ADMIN) {
      return academiaRepository.findAllByOrderByNomeAsc();
    }
    if (usuario.getRole() != Role.PROFESSOR) {
      return List.of();
    }

    LinkedHashMap<Long, Academia> academias = new LinkedHashMap<>();
    for (ProfessorAcademia vinculo : professorAcademiaRepository.findAllByProfessorIdAndAtivoTrueOrderByAcademiaNomeAsc(usuarioId)) {
      academias.put(vinculo.getAcademia().getId(), vinculo.getAcademia());
    }
    if (usuario.getAcademia() != null) {
      academias.putIfAbsent(usuario.getAcademia().getId(), usuario.getAcademia());
    }
    return new ArrayList<>(academias.values());
  }

  @Transactional(readOnly = true)
  public void validarProfessorGerenciaAcademia(Long usuarioId, Long academiaId) {
    if (!professorGerenciaAcademia(usuarioId, academiaId)) {
      throw new ForbiddenException("Academia does not belong to current professor");
    }
  }

  @Transactional
  public void vincularProfessorResponsavel(Usuario professor, Academia academia) {
    vincularProfessorAcademia(professor, academia, ProfessorAcademiaPapel.RESPONSAVEL);
  }

  @Transactional
  public void vincularProfessorAcademia(Long professorId, Long academiaId, ProfessorAcademiaPapel papel) {
    Usuario professor = usuarioRepository.findById(professorId).orElseThrow(() -> new NotFoundException("Professor not found"));
    Academia academia = academiaRepository.findById(academiaId).orElseThrow(() -> new NotFoundException("Academia not found"));
    vincularProfessorAcademia(professor, academia, papel);
  }

  @Transactional
  public void garantirVinculoProfessorAcademia(Long professorId, Long academiaId) {
    vincularProfessorAcademia(professorId, academiaId, ProfessorAcademiaPapel.RESPONSAVEL);
  }

  @Transactional
  public void vincularProfessorAcademia(Usuario professor, Academia academia, ProfessorAcademiaPapel papel) {
    if (professor == null || professor.getRole() != Role.PROFESSOR || academia == null || academia.getId() == null) {
      return;
    }
    ProfessorAcademia vinculo = professorAcademiaRepository
        .findByProfessorIdAndAcademiaId(professor.getId(), academia.getId())
        .orElseGet(ProfessorAcademia::new);
    vinculo.setProfessor(professor);
    vinculo.setAcademia(academia);
    vinculo.setPapel(papel == null ? ProfessorAcademiaPapel.RESPONSAVEL : papel);
    vinculo.setAtivo(true);
    professorAcademiaRepository.save(vinculo);
  }
}
