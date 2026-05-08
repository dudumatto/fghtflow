package com.fightflow.controller;

import com.fightflow.dto.common.ApiPage;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.matricula.MatriculaCreateRequest;
import com.fightflow.dto.matricula.MatriculaResponse;
import com.fightflow.dto.matricula.MatriculaUpdateRequest;
import com.fightflow.entity.MatriculaStatus;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.MatriculaService;
import com.fightflow.util.PageUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {
  private final MatriculaService matriculaService;

  public MatriculaController(MatriculaService matriculaService) {
    this.matriculaService = matriculaService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<MatriculaResponse>> create(@Valid @RequestBody MatriculaCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(matriculaService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MatriculaResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(matriculaService.get(SecurityUtil.currentUser(), id)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<ApiPage<MatriculaResponse>>> list(
      @RequestParam(required = false) Long alunoId,
      @RequestParam(required = false) MatriculaStatus status,
      @PageableDefault(size = 20, sort = "dataInicio", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<MatriculaResponse> page = matriculaService.list(SecurityUtil.currentUser(), alunoId, status, pageable);
    return ResponseEntity.ok(ApiResponse.ok(PageUtil.toApiPage(page, x -> x)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MatriculaResponse>> update(
      @PathVariable Long id,
      @Valid @RequestBody MatriculaUpdateRequest req
  ) {
    return ResponseEntity.ok(ApiResponse.ok(matriculaService.update(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
    matriculaService.cancel(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}

