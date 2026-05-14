package com.fightflow.service;

import com.fightflow.dto.graduacao.GraduacaoCreateRequest;
import com.fightflow.dto.graduacao.GraduacaoResponse;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Faixa;
import com.fightflow.entity.Graduacao;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.GraduacaoRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GraduacaoService {
  private final GraduacaoRepository graduacaoRepository;
  private final AlunoRepository alunoRepository;
  private final UsuarioRepository usuarioRepository;

  public GraduacaoService(GraduacaoRepository graduacaoRepository, AlunoRepository alunoRepository, UsuarioRepository usuarioRepository) {
    this.graduacaoRepository = graduacaoRepository;
    this.alunoRepository = alunoRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional
  public GraduacaoResponse create(UserPrincipal me, GraduacaoCreateRequest req) {
    assertProfessorOrAdmin(me);
    assertAcademia(me);

    Aluno aluno = alunoRepository.findById(req.alunoId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertAlunoInAcademia(me, aluno);

    Graduacao latest = graduacaoRepository.findFirstByAlunoIdOrderByDataGraduacaoDesc(aluno.getId()).orElse(null);
    boolean foraDeOrdem = latest != null && isForaDeOrdem(latest, req.faixa(), req.grau());
    if (foraDeOrdem && (req.observacao() == null || req.observacao().trim().isEmpty())) {
      throw new BadRequestException("Graduacao fora de ordem exige observacao");
    }

    Graduacao g = new Graduacao();
    g.setAluno(aluno);
    g.setFaixaEnum(req.faixa());
    g.setGrau(req.grau());
    g.setDataGraduacao(req.dataGraduacao());
    g.setProfessorResponsavel(usuarioRepository.getReferenceById(me.getId()));
    g.setObservacao(req.observacao());
    g = graduacaoRepository.save(g);

    return toResponse(g, foraDeOrdem);
  }

  @Transactional(readOnly = true)
  public List<GraduacaoResponse> list(UserPrincipal me, Long alunoId) {
    if (me.getRole() == Role.ALUNO || me.getRole() == Role.ATLETA) {
      Aluno aluno = alunoRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
          .orElseThrow(() -> new NotFoundException("Aluno not found"));
      if (alunoId != null && !aluno.getId().equals(alunoId)) {
        throw new ForbiddenException("Cannot view other aluno history");
      }
      return graduacaoRepository.findAllByAlunoIdOrderByDataGraduacaoDesc(aluno.getId())
          .stream().map(g -> toResponse(g, false)).toList();
    }

    assertProfessorOrAdmin(me);
    assertAcademia(me);
    if (alunoId == null) {
      throw new BadRequestException("alunoId is required");
    }
    Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
    assertAlunoInAcademia(me, aluno);
    return graduacaoRepository.findAllByAlunoIdOrderByDataGraduacaoDesc(alunoId)
        .stream().map(g -> toResponse(g, false)).toList();
  }

  private void assertProfessorOrAdmin(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can manage graduacoes");
    }
  }

  private void assertAcademia(UserPrincipal me) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private void assertAlunoInAcademia(UserPrincipal me, Aluno aluno) {
    if (aluno.getAcademia() == null || aluno.getAcademia().getId() == null || !aluno.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aluno is not in your academia");
    }
  }

  private boolean isForaDeOrdem(Graduacao latest, Faixa nextFaixa, int nextGrau) {
    Faixa currentFaixa = latest.getFaixaEnum();
    int currentGrau = latest.getGrau();
    if (currentFaixa == null) return false;

    // Expected: same faixa with grau+1, or next faixa with grau=0.
    boolean expectedSameFaixa = nextFaixa == currentFaixa && nextGrau == currentGrau + 1;
    boolean expectedNextFaixa = nextFaixa.ordinal() == currentFaixa.ordinal() + 1 && nextGrau == 0;
    return !(expectedSameFaixa || expectedNextFaixa);
  }

  private GraduacaoResponse toResponse(Graduacao g, boolean foraDeOrdem) {
    return new GraduacaoResponse(
        g.getId(),
        g.getAluno().getId(),
        g.getFaixaEnum(),
        g.getGrau(),
        g.getDataGraduacao(),
        g.getProfessorResponsavel().getId(),
        g.getObservacao(),
        foraDeOrdem
    );
  }
}
