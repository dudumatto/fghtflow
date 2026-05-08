package com.fightflow.controller;

import com.fightflow.dto.aula.PresencaAulaCreateRequest;
import com.fightflow.dto.aula.PresencaAulaResponse;
import com.fightflow.dto.aula.PresencaAulaUpdateRequest;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.PresencaAulaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aulas/{aulaId}/presencas")
public class AulaPresencaController {
  private final PresencaAulaService presencaAulaService;

  public AulaPresencaController(PresencaAulaService presencaAulaService) {
    this.presencaAulaService = presencaAulaService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<PresencaAulaResponse>>> list(@PathVariable Long aulaId) {
    return ResponseEntity.ok(ApiResponse.ok(presencaAulaService.list(SecurityUtil.currentUser(), aulaId)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PresencaAulaResponse>> create(@PathVariable Long aulaId, @Valid @RequestBody PresencaAulaCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(presencaAulaService.create(SecurityUtil.currentUser(), aulaId, req)));
  }

  @PutMapping
  public ResponseEntity<ApiResponse<PresencaAulaResponse>> update(@PathVariable Long aulaId, @Valid @RequestBody PresencaAulaUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(presencaAulaService.update(SecurityUtil.currentUser(), aulaId, req)));
  }
}

