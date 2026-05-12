package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.common.SelectOptionResponse;
import com.fightflow.dto.professor.ProfessorRequest;
import com.fightflow.dto.professor.ProfessorResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.ProfessorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professores")
public class ProfessorController {
  private final ProfessorService professorService;

  public ProfessorController(ProfessorService professorService) {
    this.professorService = professorService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ProfessorResponse>> create(@Valid @RequestBody ProfessorRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(professorService.create(SecurityUtil.currentUser(), req)));
  }

  @GetMapping("/select")
  public ResponseEntity<ApiResponse<List<SelectOptionResponse>>> select() {
    return ResponseEntity.ok(ApiResponse.ok(professorService.select(SecurityUtil.currentUser())));
  }
}
