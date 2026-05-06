package com.fightflow.service;

import com.fightflow.dto.documento.DocumentoResponse;
import com.fightflow.entity.Documento;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.exception.PayloadTooLargeException;
import com.fightflow.exception.UnsupportedMediaTypeException;
import com.fightflow.repository.DocumentoRepository;
import com.fightflow.repository.UsuarioRepository;
import com.fightflow.security.UserPrincipal;
import com.fightflow.util.FilenameUtil;
import com.fightflow.util.UploadRules;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentoService {
  private final DocumentoRepository documentoRepository;
  private final UsuarioRepository usuarioRepository;
  private final long maxBytes;
  private final Path rootDir;

  public DocumentoService(
      DocumentoRepository documentoRepository,
      UsuarioRepository usuarioRepository,
      @Value("${fightflow.upload.maxBytes:" + UploadRules.MAX_BYTES_DEFAULT + "}") long maxBytes,
      @Value("${fightflow.upload.rootDir:uploads}") String rootDir
  ) {
    this.documentoRepository = documentoRepository;
    this.usuarioRepository = usuarioRepository;
    this.maxBytes = maxBytes;
    this.rootDir = Path.of(rootDir);
  }

  public DocumentoResponse upload(UserPrincipal me, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File is required");
    }
    if (file.getSize() > maxBytes) {
      throw new PayloadTooLargeException("File too large");
    }
    String original = file.getOriginalFilename();
    String ext = FilenameUtil.extensionLower(original);
    if (!UploadRules.ALLOWED_EXTENSIONS.contains(ext)) {
      throw new UnsupportedMediaTypeException("Invalid extension");
    }
    String mime = file.getContentType();
    if (mime == null || !UploadRules.ALLOWED_MIME_TYPES.contains(mime)) {
      throw new UnsupportedMediaTypeException("Invalid MIME type");
    }

    String storedName = UUID.randomUUID() + "." + ext;
    Path userDir = rootDir.resolve(String.valueOf(me.getId()));
    Path target = userDir.resolve(storedName);

    try {
      Files.createDirectories(userDir);
      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new BadRequestException("Could not store file");
    }

    Documento d = new Documento();
    d.setOwner(usuarioRepository.getReferenceById(me.getId()));
    d.setOriginalName(original == null ? "documento." + ext : original);
    d.setStoredName(storedName);
    d.setMimeType(mime);
    d.setSizeBytes(file.getSize());
    d = documentoRepository.save(d);
    return toResponse(d);
  }

  public List<DocumentoResponse> listMine(UserPrincipal me) {
    return documentoRepository.findAllByOwnerIdOrderByCreatedAtDesc(me.getId()).stream().map(this::toResponse).toList();
  }

  public Resource download(UserPrincipal me, Long documentoId) {
    Documento d = documentoRepository.findByIdAndOwnerId(documentoId, me.getId())
        .orElseThrow(() -> new NotFoundException("Documento not found"));
    return toResource(me.getId(), d.getStoredName());
  }

  public Resource previewPdf(UserPrincipal me, Long documentoId) {
    Documento d = documentoRepository.findByIdAndOwnerId(documentoId, me.getId())
        .orElseThrow(() -> new NotFoundException("Documento not found"));
    if (!"application/pdf".equalsIgnoreCase(d.getMimeType())) {
      throw new UnsupportedMediaTypeException("Preview only available for PDF");
    }
    return toResource(me.getId(), d.getStoredName());
  }

  private Resource toResource(Long ownerId, String storedName) {
    try {
      Path p = rootDir.resolve(String.valueOf(ownerId)).resolve(storedName).normalize();
      Resource r = new UrlResource(p.toUri());
      if (!r.exists() || !r.isReadable()) {
        throw new NotFoundException("File not found");
      }
      return r;
    } catch (Exception e) {
      throw new NotFoundException("File not found");
    }
  }

  private DocumentoResponse toResponse(Documento d) {
    return new DocumentoResponse(d.getId(), d.getOriginalName(), d.getMimeType(), d.getSizeBytes(), d.getCreatedAt());
  }
}

