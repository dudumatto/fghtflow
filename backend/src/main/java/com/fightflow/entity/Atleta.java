package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "atletas")
public class Atleta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false, unique = true,
      foreignKey = @ForeignKey(name = "fk_atleta_usuario"))
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academia_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_atleta_academia"))
  private Academia academia;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aluno_id", unique = true,
      foreignKey = @ForeignKey(name = "fk_atleta_aluno"))
  private Aluno aluno;

  @Column(length = 30)
  private String faixa;

  private Double peso;

  @Column(length = 60)
  private String categoria;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Academia getAcademia() {
    return academia;
  }

  public void setAcademia(Academia academia) {
    this.academia = academia;
  }

  public Aluno getAluno() {
    return aluno;
  }

  public void setAluno(Aluno aluno) {
    this.aluno = aluno;
  }

  public String getFaixa() {
    return faixa;
  }

  public void setFaixa(String faixa) {
    this.faixa = faixa;
  }

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
