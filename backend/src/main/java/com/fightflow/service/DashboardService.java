package com.fightflow.service;

import com.fightflow.dto.dashboard.AulasDashboardResponse;
import com.fightflow.dto.dashboard.AdminDashboardResponse;
import com.fightflow.dto.dashboard.AlunosDashboardResponse;
import com.fightflow.dto.dashboard.AtletaDashboardResponse;
import com.fightflow.dto.dashboard.FinanceiroDashboardResponse;
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
import com.fightflow.repository.MatriculaRepository;
import com.fightflow.repository.MensalidadeRepository;
import com.fightflow.repository.PlanoRepository;
import com.fightflow.repository.PresencaAulaRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

@Service
public class DashboardService {
  private final AtletaRepository atletaRepository;
  private final LutaRepository lutaRepository;
  private final FinanceiroBloqueioService financeiroBloqueioService;
  private final AulaRepository aulaRepository;
  private final PresencaAulaRepository presencaAulaRepository;
  private final AlunoRepository alunoRepository;
  private final UsuarioRepository usuarioRepository;
  private final PlanoRepository planoRepository;
  private final MatriculaRepository matriculaRepository;
  private final MensalidadeRepository mensalidadeRepository;
  private final int diasToleranciaInadimplencia;

  public DashboardService(
      AtletaRepository atletaRepository,
      LutaRepository lutaRepository,
      FinanceiroBloqueioService financeiroBloqueioService,
      AulaRepository aulaRepository,
      PresencaAulaRepository presencaAulaRepository,
      AlunoRepository alunoRepository,
      UsuarioRepository usuarioRepository,
      PlanoRepository planoRepository,
      MatriculaRepository matriculaRepository,
      MensalidadeRepository mensalidadeRepository,
      @org.springframework.beans.factory.annotation.Value("${fightflow.financeiro.diasToleranciaInadimplencia:5}") int diasToleranciaInadimplencia
  ) {
    this.atletaRepository = atletaRepository;
    this.lutaRepository = lutaRepository;
    this.financeiroBloqueioService = financeiroBloqueioService;
    this.aulaRepository = aulaRepository;
    this.presencaAulaRepository = presencaAulaRepository;
    this.alunoRepository = alunoRepository;
    this.usuarioRepository = usuarioRepository;
    this.planoRepository = planoRepository;
    this.matriculaRepository = matriculaRepository;
    this.mensalidadeRepository = mensalidadeRepository;
    this.diasToleranciaInadimplencia = diasToleranciaInadimplencia;
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

  public AdminDashboardResponse adminDashboard(UserPrincipal me) {
    requireStaffWithAcademia(me);
    Instant now = Instant.now();

    long alunosAtivos = alunoRepository.countByAcademiaIdAndAtivoTrue(me.getAcademiaId());
    long usuariosAtletas = usuarioRepository.countByAcademiaIdAndRole(me.getAcademiaId(), Role.ATLETA);
    long usuariosProfessores = usuarioRepository.countByAcademiaIdAndRole(me.getAcademiaId(), Role.PROFESSOR);
    long usuariosAdmins = usuarioRepository.countByAcademiaIdAndRole(me.getAcademiaId(), Role.ADMIN);
    long planosAtivos = planoRepository.countByAcademiaIdAndAtivoTrue(me.getAcademiaId());
    long matriculasAtivas = matriculaRepository.countByAlunoAcademiaIdAndStatus(me.getAcademiaId(), com.fightflow.entity.MatriculaStatus.ATIVA);
    long matriculasBloqueadas = matriculaRepository.countByAlunoAcademiaIdAndStatus(me.getAcademiaId(), com.fightflow.entity.MatriculaStatus.BLOQUEADA);
    long aulasProximas = aulaRepository.countByAcademiaIdAndAtivaTrueAndDataHoraInicioGreaterThanEqual(me.getAcademiaId(), now);

    return new AdminDashboardResponse(
        alunosAtivos,
        usuariosAtletas,
        usuariosProfessores,
        usuariosAdmins,
        planosAtivos,
        matriculasAtivas,
        matriculasBloqueadas,
        aulasProximas,
        now
    );
  }

  public FinanceiroDashboardResponse financeiroDashboard(UserPrincipal me) {
    requireStaffWithAcademia(me);
    Instant now = Instant.now();

    long pendentes = mensalidadeRepository.countByAlunoAcademiaIdAndStatus(me.getAcademiaId(), com.fightflow.entity.MensalidadeStatus.PENDENTE);
    long atrasadas = mensalidadeRepository.countByAlunoAcademiaIdAndStatus(me.getAcademiaId(), com.fightflow.entity.MensalidadeStatus.ATRASADO);

    BigDecimal totalPendente = mensalidadeRepository.sumValorByAcademiaIdAndStatus(me.getAcademiaId(), com.fightflow.entity.MensalidadeStatus.PENDENTE);
    BigDecimal totalAtrasado = mensalidadeRepository.sumValorByAcademiaIdAndStatus(me.getAcademiaId(), com.fightflow.entity.MensalidadeStatus.ATRASADO);

    Instant monthStart = LocalDate.now(ZoneOffset.UTC).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant monthEnd = now;
    long pagasNoMes = mensalidadeRepository.countPagasByAcademiaIdBetween(me.getAcademiaId(), com.fightflow.entity.MensalidadeStatus.PAGO, monthStart, monthEnd);
    BigDecimal receitaNoMes = mensalidadeRepository.sumValorByAcademiaIdAndStatusBetween(me.getAcademiaId(), com.fightflow.entity.MensalidadeStatus.PAGO, monthStart, monthEnd);

    Instant cutoff = now.minus(diasToleranciaInadimplencia, java.time.temporal.ChronoUnit.DAYS);
    long inadimplentes = mensalidadeRepository.countDistinctAlunosInadimplenciaBloqueante(
        me.getAcademiaId(),
        List.of(com.fightflow.entity.MensalidadeStatus.PENDENTE, com.fightflow.entity.MensalidadeStatus.ATRASADO),
        cutoff
    );

    return new FinanceiroDashboardResponse(
        pendentes,
        atrasadas,
        pagasNoMes,
        totalPendente,
        totalAtrasado,
        receitaNoMes,
        inadimplentes,
        diasToleranciaInadimplencia,
        now
    );
  }

  public AlunosDashboardResponse alunosDashboard(UserPrincipal me) {
    requireStaffWithAcademia(me);
    Instant now = Instant.now();

    long alunosAtivos = alunoRepository.countByAcademiaIdAndAtivoTrue(me.getAcademiaId());
    Instant from30d = now.minus(30, java.time.temporal.ChronoUnit.DAYS);
    long novos30d = alunoRepository.countByAcademiaIdAndAtivoTrueAndCreatedAtGreaterThanEqual(me.getAcademiaId(), from30d);

    Instant cutoff = now.minus(diasToleranciaInadimplencia, java.time.temporal.ChronoUnit.DAYS);
    long inadimplentes = mensalidadeRepository.countDistinctAlunosInadimplenciaBloqueante(
        me.getAcademiaId(),
        List.of(com.fightflow.entity.MensalidadeStatus.PENDENTE, com.fightflow.entity.MensalidadeStatus.ATRASADO),
        cutoff
    );

    var top = mensalidadeRepository.topInadimplentesByAcademia(
        me.getAcademiaId(),
        List.of(com.fightflow.entity.MensalidadeStatus.PENDENTE, com.fightflow.entity.MensalidadeStatus.ATRASADO),
        cutoff,
        PageRequest.of(0, 5)
    );

    return new AlunosDashboardResponse(alunosAtivos, novos30d, inadimplentes, top, now);
  }

  private void requireStaffWithAcademia(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can access this dashboard");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }
}
