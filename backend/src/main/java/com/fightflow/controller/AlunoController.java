package com.fightflow.controller;

import com.fightflow.dto.aluno.AlunoRequest;
import com.fightflow.dto.aluno.AlunoResponse;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.common.SelectOptionResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.AlunoService;
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
@RequestMapping("/alunos")
public class AlunoController {
  private final AlunoService alunoService;

  public AlunoController(AlunoService alunoService) {
    this.alunoService = alunoService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AlunoResponse>> create(@Valid @RequestBody AlunoRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(alunoService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AlunoResponse>>> list() {
    return ResponseEntity.ok(ApiResponse.ok(alunoService.list(SecurityUtil.currentUser())));
  }

  @GetMapping("/select")
  public ResponseEntity<ApiResponse<List<SelectOptionResponse>>> select() {
    return ResponseEntity.ok(ApiResponse.ok(alunoService.select(SecurityUtil.currentUser())));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AlunoResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(alunoService.get(SecurityUtil.currentUser(), id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AlunoResponse>> update(@PathVariable Long id, @Valid @RequestBody AlunoRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(alunoService.update(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    alunoService.softDelete(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}
