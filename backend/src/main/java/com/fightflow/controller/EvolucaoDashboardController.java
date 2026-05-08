package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.dashboard.EvolucaoDashboardResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.EvolucaoDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard/evolucao")
public class EvolucaoDashboardController {
  private final EvolucaoDashboardService evolucaoDashboardService;

  public EvolucaoDashboardController(EvolucaoDashboardService evolucaoDashboardService) {
    this.evolucaoDashboardService = evolucaoDashboardService;
  }

  @GetMapping("/aluno/{alunoId}")
  public ResponseEntity<ApiResponse<EvolucaoDashboardResponse>> aluno(@PathVariable Long alunoId) {
    return ResponseEntity.ok(ApiResponse.ok(evolucaoDashboardService.dashboardForAluno(SecurityUtil.currentUser(), alunoId)));
  }
}

