package com.fightflow.service;

import com.fightflow.dto.dashboard.AulasDashboardResponse;
import com.fightflow.dto.dashboard.AtletaDashboardResponse;
import com.fightflow.entity.Aula;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.PresencaAulaStatus;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.AulaRepository;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.LutaRepository;
import com.fightflow.repository.PresencaAulaRepository;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
  private final AtletaRepository atletaRepository;
  private final LutaRepository lutaRepository;
  private final FinanceiroBloqueioService financeiroBloqueioService;
  private final AulaRepository aulaRepository;
  private final PresencaAulaRepository presencaAulaRepository;
  private final AlunoRepository alunoRepository;

  public DashboardService(
      AtletaRepository atletaRepository,
      LutaRepository lutaRepository,
      FinanceiroBloqueioService financeiroBloqueioService,
      AulaRepository aulaRepository,
      PresencaAulaRepository presencaAulaRepository,
      AlunoRepository alunoRepository
  ) {
    this.atletaRepository = atletaRepository;
    this.lutaRepository = lutaRepository;
    this.financeiroBloqueioService = financeiroBloqueioService;
    this.aulaRepository = aulaRepository;
    this.presencaAulaRepository = presencaAulaRepository;
    this.alunoRepository = alunoRepository;
  }

  public AtletaDashboardResponse atletaDashboard(UserPrincipal me) {
    if (me.getRole() != Role.ATLETA && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only ATLETA can access this dashboard");
    }
    Atleta a = atletaRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    long total = lutaRepository.countByAtletaId(a.getId());
    long wins = lutaRepository.countWins(a.getId());
    long losses = lutaRepository.countLosses(a.getId());
    long subWins = lutaRepository.countSubmissionWins(a.getId());

    double winrate = total == 0 ? 0.0 : (wins * 1.0) / total;
    double submissionRate = total == 0 ? 0.0 : (subWins * 1.0) / total;
    return new AtletaDashboardResponse(total, wins, losses, winrate, submissionRate, financeiroBloqueioService.status(a.getAluno()));
  }

  public AulasDashboardResponse aulasDashboard(UserPrincipal me) {
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
    Instant now = Instant.now();
    long upcoming = aulaRepository.countByAcademiaIdAndAtivaTrueAndDataHoraInicioGreaterThanEqual(me.getAcademiaId(), now);
    Instant nextAt = null;
    Aula next = aulaRepository.findFirstByAcademiaIdAndAtivaTrueAndDataHoraInicioGreaterThanEqualOrderByDataHoraInicioAsc(me.getAcademiaId(), now);
    if (next != null) nextAt = next.getDataHoraInicio();

    if (me.getRole() == Role.ATLETA) {
      Aluno aluno = alunoRepository.findByUsuarioIdWithUsuarioAndAcademia(me.getId())
          .orElseThrow(() -> new NotFoundException("Aluno not found"));
      if (!aluno.getAcademia().getId().equals(me.getAcademiaId())) {
        throw new ForbiddenException("Aluno is not in your academia");
      }
      long presentes = presencaAulaRepository.countByAlunoIdAndStatus(aluno.getId(), PresencaAulaStatus.PRESENTE);
      long ausentes = presencaAulaRepository.countByAlunoIdAndStatus(aluno.getId(), PresencaAulaStatus.AUSENTE);
      long just = presencaAulaRepository.countByAlunoIdAndStatus(aluno.getId(), PresencaAulaStatus.JUSTIFICADO);
      return new AulasDashboardResponse(upcoming, presentes, ausentes, just, nextAt);
    }

    if (me.getRole() == Role.PROFESSOR || me.getRole() == Role.ADMIN) {
      long presentes = presencaAulaRepository.countByAulaAcademiaIdAndStatus(me.getAcademiaId(), PresencaAulaStatus.PRESENTE);
      long ausentes = presencaAulaRepository.countByAulaAcademiaIdAndStatus(me.getAcademiaId(), PresencaAulaStatus.AUSENTE);
      long just = presencaAulaRepository.countByAulaAcademiaIdAndStatus(me.getAcademiaId(), PresencaAulaStatus.JUSTIFICADO);
      return new AulasDashboardResponse(upcoming, presentes, ausentes, just, nextAt);
    }

    throw new ForbiddenException("Forbidden");
  }
}
