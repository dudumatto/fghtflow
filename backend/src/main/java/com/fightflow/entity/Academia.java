package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "academias")
public class Academia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nome;

  @Column(length = 240)
  private String endereco;

  @Column(nullable = false)
  private boolean ativo = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "professor_responsavel_usuario_id",
      foreignKey = @ForeignKey(name = "fk_academia_professor_responsavel"))
  private Usuario professorResponsavel;

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

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  public Usuario getProfessorResponsavel() {
    return professorResponsavel;
  }

  public void setProfessorResponsavel(Usuario professorResponsavel) {
    this.professorResponsavel = professorResponsavel;
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
