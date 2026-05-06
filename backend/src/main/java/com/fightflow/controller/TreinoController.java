package com.fightflow.controller;

import com.fightflow.dto.common.ApiPage;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.treino.TreinoCreateRequest;
import com.fightflow.dto.treino.TreinoResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.TreinoService;
import com.fightflow.util.PageUtil;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/treinos")
public class TreinoController {
  private final TreinoService treinoService;

  public TreinoController(TreinoService treinoService) {
    this.treinoService = treinoService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TreinoResponse>> create(@Valid @RequestBody TreinoCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(treinoService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<ApiPage<TreinoResponse>>> list(
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant dateTo,
      @PageableDefault(size = 20, sort = "startsAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<TreinoResponse> page = treinoService.list(SecurityUtil.currentUser(), dateFrom, dateTo, pageable);
    return ResponseEntity.ok(ApiResponse.ok(PageUtil.toApiPage(page, x -> x)));
  }
}
