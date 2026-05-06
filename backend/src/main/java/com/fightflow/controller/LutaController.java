package com.fightflow.controller;

import com.fightflow.dto.common.ApiPage;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.luta.LutaCreateRequest;
import com.fightflow.dto.luta.LutaResponse;
import com.fightflow.entity.LutaResultado;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.LutaService;
import com.fightflow.util.PageUtil;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/lutas")
public class LutaController {
  private final LutaService lutaService;

  public LutaController(LutaService lutaService) {
    this.lutaService = lutaService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<LutaResponse>> create(@Valid @RequestBody LutaCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(lutaService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<ApiPage<LutaResponse>>> list(
      @RequestParam(required = false) Long atletaId,
      @RequestParam(required = false) LutaResultado resultado,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant dateTo,
      @PageableDefault(size = 20, sort = "foughtAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<LutaResponse> page = lutaService.list(SecurityUtil.currentUser(), atletaId, resultado, dateFrom, dateTo, pageable);
    return ResponseEntity.ok(ApiResponse.ok(PageUtil.toApiPage(page, x -> x)));
  }
}
