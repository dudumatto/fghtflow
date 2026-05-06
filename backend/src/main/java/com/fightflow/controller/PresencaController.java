package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.presenca.PresencaCreateRequest;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.PresencaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/presencas")
public class PresencaController {
  private final PresencaService presencaService;

  public PresencaController(PresencaService presencaService) {
    this.presencaService = presencaService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registrar(@Valid @RequestBody PresencaCreateRequest req) {
    presencaService.registrar(SecurityUtil.currentUser(), req);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}
