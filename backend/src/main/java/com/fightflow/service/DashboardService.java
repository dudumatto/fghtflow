package com.fightflow.service;

import com.fightflow.dto.dashboard.AtletaDashboardResponse;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Role;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.LutaRepository;
import com.fightflow.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
  private final AtletaRepository atletaRepository;
  private final LutaRepository lutaRepository;

  public DashboardService(AtletaRepository atletaRepository, LutaRepository lutaRepository) {
    this.atletaRepository = atletaRepository;
    this.lutaRepository = lutaRepository;
  }

  public AtletaDashboardResponse atletaDashboard(UserPrincipal me) {
    if (me.getRole() != Role.ATLETA && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only ATLETA can access this dashboard");
    }
    Atleta a = atletaRepository.findByUsuarioId(me.getId()).orElseThrow(() -> new NotFoundException("Atleta not found"));
    long total = lutaRepository.countByAtletaId(a.getId());
    long wins = lutaRepository.countWins(a.getId());
    long losses = lutaRepository.countLosses(a.getId());
    long subWins = lutaRepository.countSubmissionWins(a.getId());

    double winrate = total == 0 ? 0.0 : (wins * 1.0) / total;
    double submissionRate = total == 0 ? 0.0 : (subWins * 1.0) / total;
    return new AtletaDashboardResponse(total, wins, losses, winrate, submissionRate);
  }
}

