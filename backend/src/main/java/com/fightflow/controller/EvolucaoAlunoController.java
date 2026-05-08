package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.evolucao.EvolucaoAlunoCreateRequest;
import com.fightflow.dto.evolucao.EvolucaoAlunoResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.EvolucaoAlunoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evolucoes")
public class EvolucaoAlunoController {
  private final EvolucaoAlunoService evolucaoAlunoService;

  public EvolucaoAlunoController(EvolucaoAlunoService evolucaoAlunoService) {
    this.evolucaoAlunoService = evolucaoAlunoService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<EvolucaoAlunoResponse>> create(@Valid @RequestBody EvolucaoAlunoCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(evolucaoAlunoService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<EvolucaoAlunoResponse>>> list(@RequestParam(required = false) Long alunoId) {
    return ResponseEntity.ok(ApiResponse.ok(evolucaoAlunoService.list(SecurityUtil.currentUser(), alunoId)));
  }
}

