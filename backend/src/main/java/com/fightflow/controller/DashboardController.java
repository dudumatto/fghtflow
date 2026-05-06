package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.dashboard.AtletaDashboardResponse;
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
}
