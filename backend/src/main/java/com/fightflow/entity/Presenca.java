package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "presencas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_presenca_treino_atleta",
        columnNames = {"treino_id", "atleta_id"}))
public class Presenca {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "treino_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_presenca_treino"))
  private Treino treino;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "atleta_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_presenca_atleta"))
  private Atleta atleta;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Treino getTreino() {
    return treino;
  }

  public void setTreino(Treino treino) {
    this.treino = treino;
  }

  public Atleta getAtleta() {
    return atleta;
  }

  public void setAtleta(Atleta atleta) {
    this.atleta = atleta;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

