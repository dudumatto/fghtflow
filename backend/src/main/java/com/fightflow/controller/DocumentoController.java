package com.fightflow.controller;

import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.documento.DocumentoResponse;
import com.fightflow.security.SecurityUtil;
import com.fightflow.service.DocumentoService;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {
  private final DocumentoService documentoService;

  public DocumentoController(DocumentoService documentoService) {
    this.documentoService = documentoService;
  }

  @PostMapping("/upload")
  public ResponseEntity<ApiResponse<DocumentoResponse>> upload(@RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(ApiResponse.ok(documentoService.upload(SecurityUtil.currentUser(), file)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<DocumentoResponse>>> list() {
    return ResponseEntity.ok(ApiResponse.ok(documentoService.listMine(SecurityUtil.currentUser())));
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable Long id) {
    Resource r = documentoService.download(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documento\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(r);
  }

  @GetMapping("/{id}/preview")
  public ResponseEntity<Resource> preview(@PathVariable Long id) {
    Resource r = documentoService.previewPdf(SecurityUtil.currentUser(), id);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
        .contentType(MediaType.APPLICATION_PDF)
        .body(r);
  }
}
