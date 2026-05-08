package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.graduacao.GraduacaoCreateRequest;
import com.fightflow.dto.graduacao.GraduacaoResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.GraduacaoService;
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
@RequestMapping("/graduacoes")
public class GraduacaoController {
  private final GraduacaoService graduacaoService;

  public GraduacaoController(GraduacaoService graduacaoService) {
    this.graduacaoService = graduacaoService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<GraduacaoResponse>> create(@Valid @RequestBody GraduacaoCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(graduacaoService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<GraduacaoResponse>>> list(@RequestParam(required = false) Long alunoId) {
    return ResponseEntity.ok(ApiResponse.ok(graduacaoService.list(SecurityUtil.currentUser(), alunoId)));
  }
}

