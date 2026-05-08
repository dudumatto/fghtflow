package com.fightflow.controller;

import com.fightflow.dto.common.ApiPage;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.plano.PlanoCreateRequest;
import com.fightflow.dto.plano.PlanoResponse;
import com.fightflow.dto.plano.PlanoUpdateRequest;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.PlanoService;
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
@RequestMapping("/planos")
public class PlanoController {
  private final PlanoService planoService;

  public PlanoController(PlanoService planoService) {
    this.planoService = planoService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PlanoResponse>> create(@Valid @RequestBody PlanoCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(planoService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PlanoResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(planoService.get(SecurityUtil.currentUser(), id)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<ApiPage<PlanoResponse>>> list(
      @RequestParam(required = false) Boolean ativo,
      @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
  ) {
    Page<PlanoResponse> page = planoService.list(SecurityUtil.currentUser(), ativo, pageable);
    return ResponseEntity.ok(ApiResponse.ok(PageUtil.toApiPage(page, x -> x)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<PlanoResponse>> update(@PathVariable Long id, @Valid @RequestBody PlanoUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(planoService.update(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    planoService.delete(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}

