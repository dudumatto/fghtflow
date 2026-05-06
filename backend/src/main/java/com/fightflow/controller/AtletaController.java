package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.atleta.AtletaProfileResponse;
import com.fightflow.dto.atleta.AtletaUpdateRequest;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.AtletaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atletas")
public class AtletaController {
  private final AtletaService atletaService;

  public AtletaController(AtletaService atletaService) {
    this.atletaService = atletaService;
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<AtletaProfileResponse>> me() {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.getMe(SecurityUtil.currentUser())));
  }

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<AtletaProfileResponse>> update(@Valid @RequestBody AtletaUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.updateMe(SecurityUtil.currentUser(), req)));
  }
}
