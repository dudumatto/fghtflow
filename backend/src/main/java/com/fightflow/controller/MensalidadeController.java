package com.fightflow.controller;

import com.fightflow.dto.common.ApiPage;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.mensalidade.MensalidadeCreateRequest;
import com.fightflow.dto.mensalidade.MensalidadePagamentoRequest;
import com.fightflow.dto.mensalidade.MensalidadeResponse;
import com.fightflow.dto.mensalidade.MensalidadeUpdateRequest;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.MensalidadeService;
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
@RequestMapping("/mensalidades")
public class MensalidadeController {
  private final MensalidadeService mensalidadeService;

  public MensalidadeController(MensalidadeService mensalidadeService) {
    this.mensalidadeService = mensalidadeService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<MensalidadeResponse>> create(@Valid @RequestBody MensalidadeCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(mensalidadeService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MensalidadeResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.ok(mensalidadeService.get(SecurityUtil.currentUser(), id)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<ApiPage<MensalidadeResponse>>> list(
      @RequestParam(required = false) Long alunoId,
      @RequestParam(required = false) MensalidadeStatus status,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant vencimentoFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant vencimentoTo,
      @PageableDefault(size = 20, sort = "vencimento", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<MensalidadeResponse> page = mensalidadeService.list(
        SecurityUtil.currentUser(), alunoId, status, vencimentoFrom, vencimentoTo, pageable);
    return ResponseEntity.ok(ApiResponse.ok(PageUtil.toApiPage(page, x -> x)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MensalidadeResponse>> update(
      @PathVariable Long id,
      @Valid @RequestBody MensalidadeUpdateRequest req
  ) {
    return ResponseEntity.ok(ApiResponse.ok(mensalidadeService.update(SecurityUtil.currentUser(), id, req)));
  }

  @PostMapping("/{id}/pagamento")
  public ResponseEntity<ApiResponse<MensalidadeResponse>> registrarPagamento(
      @PathVariable Long id,
      @Valid @RequestBody MensalidadePagamentoRequest req
  ) {
    return ResponseEntity.ok(ApiResponse.ok(mensalidadeService.registrarPagamento(SecurityUtil.currentUser(), id, req)));
  }

  @PutMapping("/{id}/pagar")
  public ResponseEntity<ApiResponse<MensalidadeResponse>> pagar(
      @PathVariable Long id,
      @Valid @RequestBody MensalidadePagamentoRequest req
  ) {
    return ResponseEntity.ok(ApiResponse.ok(mensalidadeService.registrarPagamento(SecurityUtil.currentUser(), id, req)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
    mensalidadeService.cancel(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}
