package com.fightflow.service;

import com.fightflow.dto.dashboard.EvolucaoDashboardResponse;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Faixa;
import com.fightflow.entity.Graduacao;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.EvolucaoAlunoRepository;
import com.fightflow.repository.GraduacaoRepository;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EvolucaoDashboardService {
  private final AlunoRepository alunoRepository;
  private final GraduacaoRepository graduacaoRepository;
  private final EvolucaoAlunoRepository evolucaoAlunoRepository;

  public EvolucaoDashboardService(
      AlunoRepository alunoRepository,
      GraduacaoRepository graduacaoRepository,
      EvolucaoAlunoRepository evolucaoAlunoRepository
  ) {
    this.alunoRepository = alunoRepository;
    this.graduacaoRepository = graduacaoRepository;
    this.evolucaoAlunoRepository = evolucaoAlunoRepository;
  }

  public EvolucaoDashboardResponse dashboardForAluno(UserPrincipal me, Long alunoId) {
    if (alunoId == null) throw new BadRequestException("alunoId is required");

    if (me.getRole() == Role.ATLETA) {
      Aluno alunoMe = alunoRepository.findByUsuarioId(me.getId()).orElseThrow(() -> new NotFoundException("Aluno not found"));
      if (!alunoMe.getId().equals(alunoId)) throw new ForbiddenException("Forbidden");
    } else if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Forbidden");
    }

    if (me.getAcademiaId() == null) throw new BadRequestException("User has no academia");

    Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new NotFoundException("Aluno not found"));
    if (aluno.getAcademia() == null || aluno.getAcademia().getId() == null || !aluno.getAcademia().getId().equals(me.getAcademiaId())) {
      throw new ForbiddenException("Aluno is not in your academia");
    }

    Graduacao latest = graduacaoRepository.findFirstByAlunoIdOrderByDataGraduacaoDesc(alunoId).orElse(null);
    Faixa faixaAtual = latest != null && latest.getFaixaEnum() != null ? latest.getFaixaEnum() : Faixa.BRANCA;
    int grauAtual = latest != null ? latest.getGrau() : 0;
    Instant ultimaGraduacaoEm = latest != null ? latest.getDataGraduacao() : null;

    long totalEvolucoes = evolucaoAlunoRepository.countByAlunoId(alunoId);
    List<String> recomendacoes = buildRecomendacoes(faixaAtual, grauAtual, ultimaGraduacaoEm, totalEvolucoes);

    return new EvolucaoDashboardResponse(alunoId, faixaAtual, grauAtual, ultimaGraduacaoEm, totalEvolucoes, recomendacoes);
  }

  private List<String> buildRecomendacoes(Faixa faixaAtual, int grauAtual, Instant ultimaGraduacaoEm, long totalEvolucoes) {
    List<String> recs = new ArrayList<>();
    if (totalEvolucoes == 0) {
      recs.add("Registrar evolucoes semanais (tecnica, fisico e tatico) para acompanhar progresso.");
    }
    if (ultimaGraduacaoEm == null) {
      recs.add("Registrar a primeira graduacao para manter historico de faixa/grau.");
    } else {
      recs.add("Manter consistencia: validar graduacao fora de ordem com observacao.");
    }
    if (faixaAtual == Faixa.BRANCA && grauAtual == 0) {
      recs.add("Foco inicial: fundamentos, escapes e defesa de guarda.");
    }
    return recs;
  }
}

