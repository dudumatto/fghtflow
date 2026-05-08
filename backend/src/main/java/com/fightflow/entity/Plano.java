package com.fightflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "planos", indexes = {
    @Index(name = "idx_plano_academia", columnList = "academia_id")
})
public class Plano {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academia_id", foreignKey = @ForeignKey(name = "fk_plano_academia"))
  private Academia academia;

  @Column(nullable = false, length = 120)
  private String nome;

  @Column(length = 1000)
  private String descricao;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal valor;

  @Column(nullable = false)
  private int duracaoEmDias;

  @Column(nullable = false)
  private boolean ativo = true;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public int getDuracaoEmDias() {
    return duracaoEmDias;
  }

  public void setDuracaoEmDias(int duracaoEmDias) {
    this.duracaoEmDias = duracaoEmDias;
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
}
