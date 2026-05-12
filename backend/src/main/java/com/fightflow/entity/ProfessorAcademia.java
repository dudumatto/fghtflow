package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "professores_academias", indexes = {
    @Index(name = "idx_prof_academia_professor", columnList = "professor_usuario_id"),
    @Index(name = "idx_prof_academia_academia", columnList = "academia_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_professor_academia", columnNames = {"professor_usuario_id", "academia_id"})
})
public class ProfessorAcademia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "professor_usuario_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_prof_academia_professor"))
  private Usuario professor;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academia_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_prof_academia_academia"))
  private Academia academia;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ProfessorAcademiaPapel papel = ProfessorAcademiaPapel.RESPONSAVEL;

  @Column(nullable = false)
  private boolean ativo = true;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private Instant updatedAt = Instant.now();

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getProfessor() {
    return professor;
  }

  public void setProfessor(Usuario professor) {
    this.professor = professor;
  }

  public Academia getAcademia() {
    return academia;
  }

  public void setAcademia(Academia academia) {
    this.academia = academia;
  }

  public ProfessorAcademiaPapel getPapel() {
    return papel;
  }

  public void setPapel(ProfessorAcademiaPapel papel) {
    this.papel = papel;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
