package com.fightflow.repository;

import com.fightflow.entity.Documento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
  List<Documento> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerUsuarioId);
  Optional<Documento> findByIdAndOwnerId(Long id, Long ownerUsuarioId);
}

