package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alunos", indexes = {
    @Index(name = "idx_aluno_academia", columnList = "academia_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_aluno_usuario", columnNames = {"usuario_id"})
})
public class Aluno {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_aluno_usuario"))
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academia_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_aluno_academia"))
  private Academia academia;

  @Column(length = 120)
  private String nome;

  @Column(nullable = false)
  private boolean ativo = true;

  @Column(length = 30)
  private String faixaAtual;

  @Column(nullable = false)
  private int grauAtual = 0;

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

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  public String getFaixaAtual() {
    return faixaAtual;
  }

  public void setFaixaAtual(String faixaAtual) {
    this.faixaAtual = faixaAtual;
  }

  public int getGrauAtual() {
    return grauAtual;
  }

  public void setGrauAtual(int grauAtual) {
    this.grauAtual = grauAtual;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
