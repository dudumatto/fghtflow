package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lutas")
public class Luta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "atleta_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_luta_atleta"))
  private Atleta atleta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "competicao_id",
      foreignKey = @ForeignKey(name = "fk_luta_competicao"))
  private Competicao competicao;

  @Column(length = 140)
  private String adversarioNome;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private LutaResultado resultado;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LutaMetodo metodo;

  @Column(nullable = false)
  private Instant foughtAt;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Atleta getAtleta() {
    return atleta;
  }

  public void setAtleta(Atleta atleta) {
    this.atleta = atleta;
  }

  public Competicao getCompeticao() {
    return competicao;
  }

  public void setCompeticao(Competicao competicao) {
    this.competicao = competicao;
  }

  public String getAdversarioNome() {
    return adversarioNome;
  }

  public void setAdversarioNome(String adversarioNome) {
    this.adversarioNome = adversarioNome;
  }

  public LutaResultado getResultado() {
    return resultado;
  }

  public void setResultado(LutaResultado resultado) {
    this.resultado = resultado;
  }

  public LutaMetodo getMetodo() {
    return metodo;
  }

  public void setMetodo(LutaMetodo metodo) {
    this.metodo = metodo;
  }

  public Instant getFoughtAt() {
    return foughtAt;
  }

  public void setFoughtAt(Instant foughtAt) {
    this.foughtAt = foughtAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

