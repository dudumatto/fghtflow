package com.fightflow.service;

import com.fightflow.dto.auth.AuthResponse;
import com.fightflow.dto.auth.LoginRequest;
import com.fightflow.dto.auth.RegisterRequest;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  public record AuthPair(AuthResponse auth, String refreshToken) {}

  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final AtletaRepository atletaRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;

  public AuthService(
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      AtletaRepository atletaRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      RefreshTokenService refreshTokenService
  ) {
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.atletaRepository = atletaRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.refreshTokenService = refreshTokenService;
  }

  @Transactional
  public AuthPair register(RegisterRequest req) {
    if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
      throw new BadRequestException("Email already in use");
    }
    Role role = (req.role() == null) ? Role.ATLETA : req.role();

    Academia academia = resolveAcademia(role, req.academiaId(), req.academiaNome());

    Usuario u = new Usuario();
    u.setEmail(req.email().trim().toLowerCase());
    u.setPasswordHash(passwordEncoder.encode(req.password()));
    u.setRole(role);
    u.setAcademia(academia);
    u = usuarioRepository.save(u);

    if (u.getRole() == Role.ATLETA) {
      Atleta a = new Atleta();
      a.setUsuario(u);
      a.setAcademia(academia);
      a.setFaixa(req.faixa());
      a.setPeso(req.peso());
      a.setCategoria(req.categoria());
      atletaRepository.save(a);
    }

    Long academiaId = (academia == null) ? null : academia.getId();
    RefreshTokenService.Issued issued = refreshTokenService.issueForUser(u);
    return new AuthPair(new AuthResponse(issued.accessToken(), u.getId(), u.getRole(), academiaId), issued.refreshToken());
  }

  public AuthPair login(LoginRequest req) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    com.fightflow.security.UserPrincipal principal = (com.fightflow.security.UserPrincipal) auth.getPrincipal();
    Usuario u = usuarioRepository.findById(principal.getId()).orElseThrow(() -> new NotFoundException("User not found"));
    RefreshTokenService.Issued issued = refreshTokenService.issueForUser(u);
    return new AuthPair(new AuthResponse(issued.accessToken(), principal.getId(), principal.getRole(), principal.getAcademiaId()),
        issued.refreshToken());
  }

  private Academia resolveAcademia(Role role, Long academiaId, String academiaNome) {
    if (academiaId != null) {
      return academiaRepository.findById(academiaId).orElseThrow(() -> new NotFoundException("Academia not found"));
    }
    if (academiaNome != null && !academiaNome.isBlank()) {
      Academia a = new Academia();
      a.setNome(academiaNome.trim());
      return academiaRepository.save(a);
    }
    if (role == Role.PROFESSOR) {
      throw new BadRequestException("academiaId or academiaNome is required for PROFESSOR");
    }
    // ATLETA/ADMIN can exist without an academy in this minimal scaffold.
    return null;
  }
}
