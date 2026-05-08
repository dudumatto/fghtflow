package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.dashboard.AulasDashboardResponse;
import com.fightflow.dto.dashboard.AdminDashboardResponse;
import com.fightflow.dto.dashboard.AlunosDashboardResponse;
import com.fightflow.dto.dashboard.AtletaDashboardResponse;
import com.fightflow.dto.dashboard.FinanceiroDashboardResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/atleta")
  public ResponseEntity<ApiResponse<AtletaDashboardResponse>> atleta() {
    return ResponseEntity.ok(ApiResponse.ok(dashboardService.atletaDashboard(SecurityUtil.currentUser())));
  }

  @GetMapping("/aulas")
  public ResponseEntity<ApiResponse<AulasDashboardResponse>> aulas() {
    return ResponseEntity.ok(ApiResponse.ok(dashboardService.aulasDashboard(SecurityUtil.currentUser())));
  }

  @GetMapping("/admin")
  public ResponseEntity<ApiResponse<AdminDashboardResponse>> admin() {
    return ResponseEntity.ok(ApiResponse.ok(dashboardService.adminDashboard(SecurityUtil.currentUser())));
  }

  @GetMapping("/financeiro")
  public ResponseEntity<ApiResponse<FinanceiroDashboardResponse>> financeiro() {
    return ResponseEntity.ok(ApiResponse.ok(dashboardService.financeiroDashboard(SecurityUtil.currentUser())));
  }

  @GetMapping("/alunos")
  public ResponseEntity<ApiResponse<AlunosDashboardResponse>> alunos() {
    return ResponseEntity.ok(ApiResponse.ok(dashboardService.alunosDashboard(SecurityUtil.currentUser())));
  }
}
