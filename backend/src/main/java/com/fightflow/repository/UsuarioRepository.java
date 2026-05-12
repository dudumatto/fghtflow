package com.fightflow.repository;

import com.fightflow.entity.Usuario;
import com.fightflow.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);

  long countByAcademiaIdAndRole(Long academiaId, Role role);
  List<Usuario> findAllByRoleOrderByEmailAsc(Role role);
  List<Usuario> findAllByAcademiaIdInAndRoleOrderByEmailAsc(List<Long> academiaIds, Role role);
  List<Usuario> findAllByAcademiaIdAndRoleOrderByEmailAsc(Long academiaId, Role role);
}
