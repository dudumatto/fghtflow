package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.atleta.AtletaProfileResponse;
import com.fightflow.dto.atleta.AtletaRequest;
import com.fightflow.dto.atleta.AtletaResponse;
import com.fightflow.dto.atleta.AtletaUpdateRequest;
import com.fightflow.dto.common.SelectOptionResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.AtletaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping
  public ResponseEntity<ApiResponse<AtletaResponse>> create(@Valid @RequestBody AtletaRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AtletaResponse>>> list() {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.list(SecurityUtil.currentUser())));
  }

  @GetMapping("/select")
  public ResponseEntity<ApiResponse<List<SelectOptionResponse>>> select() {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.select(SecurityUtil.currentUser())));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<AtletaProfileResponse>> me() {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.getMe(SecurityUtil.currentUser())));
  }

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<AtletaProfileResponse>> update(@Valid @RequestBody AtletaUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.updateMe(SecurityUtil.currentUser(), req)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AtletaResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.get(SecurityUtil.currentUser(), id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AtletaResponse>> update(@PathVariable Long id, @Valid @RequestBody AtletaRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(atletaService.update(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    atletaService.softDelete(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}
