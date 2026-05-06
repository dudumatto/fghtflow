package com.fightflow.test;

import com.fightflow.entity.Academia;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestDataFactory {
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final AtletaRepository atletaRepository;
  private final PasswordEncoder passwordEncoder;

  public TestDataFactory(
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      AtletaRepository atletaRepository,
      PasswordEncoder passwordEncoder
  ) {
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.atletaRepository = atletaRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Usuario createAtletaUser(String email) {
    Academia ac = createAcademia("Academia Teste");
    return createAtletaUserInAcademia(email, ac);
  }

  public Academia createAcademia(String nome) {
    Academia ac = new Academia();
    ac.setNome(nome);
    return academiaRepository.save(ac);
  }

  public Usuario createAtletaUserInAcademia(String email, Academia ac) {
    Usuario u = new Usuario();
    u.setEmail(email);
    u.setPasswordHash(passwordEncoder.encode("password123"));
    u.setRole(Role.ATLETA);
    u.setAcademia(ac);
    u = usuarioRepository.save(u);

    Atleta a = new Atleta();
    a.setUsuario(u);
    a.setAcademia(ac);
    atletaRepository.save(a);

    return u;
  }

  public Usuario createProfessorUserInAcademia(String email, Academia ac) {
    Usuario u = new Usuario();
    u.setEmail(email);
    u.setPasswordHash(passwordEncoder.encode("password123"));
    u.setRole(Role.PROFESSOR);
    u.setAcademia(ac);
    return usuarioRepository.save(u);
  }
}
