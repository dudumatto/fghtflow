package com.fightflow.repository;

import com.fightflow.entity.Usuario;
import com.fightflow.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);

  long countByAcademiaIdAndRole(Long academiaId, Role role);
}
