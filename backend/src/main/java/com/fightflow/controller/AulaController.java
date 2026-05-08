package com.fightflow.controller;

import com.fightflow.dto.aula.AulaCreateRequest;
import com.fightflow.dto.aula.AulaResponse;
import com.fightflow.dto.aula.AulaUpdateRequest;
import com.fightflow.dto.common.ApiPage;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.entity.AulaTipo;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.AulaService;
import com.fightflow.util.PageUtil;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
@RequestMapping("/aulas")
public class AulaController {
  private final AulaService aulaService;

  public AulaController(AulaService aulaService) {
    this.aulaService = aulaService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AulaResponse>> create(@Valid @RequestBody AulaCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(aulaService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AulaResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(aulaService.get(SecurityUtil.currentUser(), id)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<ApiPage<AulaResponse>>> list(
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant dateTo,
      @RequestParam(required = false) AulaTipo tipo,
      @RequestParam(required = false) Boolean ativa,
      @RequestParam(required = false) Long professorUsuarioId,
      @RequestParam(required = false) String q,
      @PageableDefault(size = 20, sort = "dataHoraInicio", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<AulaResponse> page = aulaService.list(SecurityUtil.currentUser(), dateFrom, dateTo, tipo, ativa, professorUsuarioId, q, pageable);
    return ResponseEntity.ok(ApiResponse.ok(PageUtil.toApiPage(page, x -> x)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AulaResponse>> update(@PathVariable Long id, @Valid @RequestBody AulaUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(aulaService.update(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    aulaService.delete(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}

