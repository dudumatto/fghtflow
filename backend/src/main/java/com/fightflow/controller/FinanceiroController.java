package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.financeiro.AtualizarBloqueiosResponse;
import com.fightflow.dto.financeiro.AtualizarAtrasosResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.FinanceiroBloqueioService;
import com.fightflow.service.FinanceiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {
  private final FinanceiroService financeiroService;
  private final FinanceiroBloqueioService financeiroBloqueioService;

  public FinanceiroController(FinanceiroService financeiroService, FinanceiroBloqueioService financeiroBloqueioService) {
    this.financeiroService = financeiroService;
    this.financeiroBloqueioService = financeiroBloqueioService;
  }

  @PostMapping("/atualizar-atrasos")
  public ResponseEntity<ApiResponse<AtualizarAtrasosResponse>> atualizarAtrasos() {
    return ResponseEntity.ok(ApiResponse.ok(financeiroService.atualizarAtrasos(SecurityUtil.currentUser())));
  }

  @PostMapping("/atualizar-bloqueios")
  public ResponseEntity<ApiResponse<AtualizarBloqueiosResponse>> atualizarBloqueios() {
    return ResponseEntity.ok(ApiResponse.ok(financeiroBloqueioService.atualizarBloqueios(SecurityUtil.currentUser())));
  }
}
