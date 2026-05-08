package com.fightflow.test;

import com.fightflow.entity.Academia;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Role;
import com.fightflow.entity.Usuario;
import com.fightflow.repository.AcademiaRepository;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestDataFactory {
  private final UsuarioRepository usuarioRepository;
  private final AcademiaRepository academiaRepository;
  private final AlunoRepository alunoRepository;
  private final AtletaRepository atletaRepository;
  private final PasswordEncoder passwordEncoder;

  public TestDataFactory(
      UsuarioRepository usuarioRepository,
      AcademiaRepository academiaRepository,
      AlunoRepository alunoRepository,
      AtletaRepository atletaRepository,
      PasswordEncoder passwordEncoder
  ) {
    this.usuarioRepository = usuarioRepository;
    this.academiaRepository = academiaRepository;
    this.alunoRepository = alunoRepository;
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

    Aluno aluno = new Aluno();
    aluno.setUsuario(u);
    aluno.setAcademia(ac);
    aluno.setNome(email.substring(0, email.indexOf('@')));
    aluno = alunoRepository.save(aluno);

    Atleta a = new Atleta();
    a.setUsuario(u);
    a.setAcademia(ac);
    a.setAluno(aluno);
    atletaRepository.save(a);

    return u;
  }

  public Aluno findAlunoByUsuario(Usuario usuario) {
    return alunoRepository.findByUsuarioId(usuario.getId()).orElseThrow();
  }

  public Usuario createProfessorUserInAcademia(String email, Academia ac) {
    Usuario u = new Usuario();
    u.setEmail(email);
    u.setPasswordHash(passwordEncoder.encode("password123"));
    u.setRole(Role.PROFESSOR);
    u.setAcademia(ac);
    return usuarioRepository.save(u);
  }

  public Usuario createAdminUserInAcademia(String email, Academia ac) {
    Usuario u = new Usuario();
    u.setEmail(email);
    u.setPasswordHash(passwordEncoder.encode("password123"));
    u.setRole(Role.ADMIN);
    u.setAcademia(ac);
    return usuarioRepository.save(u);
  }
}
