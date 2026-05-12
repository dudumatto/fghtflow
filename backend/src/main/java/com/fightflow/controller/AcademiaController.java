package com.fightflow.controller;

import com.fightflow.dto.academia.AcademiaRequest;
import com.fightflow.dto.academia.AcademiaResponse;
import com.fightflow.dto.academia.AcademiaResumoResponse;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.AcademiaService;
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
@RequestMapping("/academias")
public class AcademiaController {
  private final AcademiaService academiaService;

  public AcademiaController(AcademiaService academiaService) {
    this.academiaService = academiaService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AcademiaResponse>> create(@Valid @RequestBody AcademiaRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(academiaService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AcademiaResponse>>> list() {
    return ResponseEntity.ok(ApiResponse.ok(academiaService.list(SecurityUtil.currentUser())));
  }

  @GetMapping("/select")
  public ResponseEntity<ApiResponse<List<AcademiaResumoResponse>>> select() {
    return ResponseEntity.ok(ApiResponse.ok(academiaService.select(SecurityUtil.currentUser())));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AcademiaResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(academiaService.get(SecurityUtil.currentUser(), id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AcademiaResponse>> update(@PathVariable Long id, @Valid @RequestBody AcademiaRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(academiaService.update(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    academiaService.softDelete(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}
