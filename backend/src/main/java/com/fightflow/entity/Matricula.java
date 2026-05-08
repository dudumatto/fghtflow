package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "matriculas", indexes = {
    @Index(name = "idx_matricula_aluno", columnList = "aluno_id"),
    @Index(name = "idx_matricula_aluno_status", columnList = "aluno_id,status")
})
public class Matricula {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matricula_aluno"))
  private Aluno aluno;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "plano_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matricula_plano"))
  private Plano plano;

  @Column(nullable = false)
  private Instant dataInicio;

  private Instant dataFim;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MatriculaStatus status = MatriculaStatus.ATIVA;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Aluno getAluno() {
    return aluno;
  }

  public void setAluno(Aluno aluno) {
    this.aluno = aluno;
  }

  public Plano getPlano() {
    return plano;
  }

  public void setPlano(Plano plano) {
    this.plano = plano;
  }

  public Instant getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(Instant dataInicio) {
    this.dataInicio = dataInicio;
  }

  public Instant getDataFim() {
    return dataFim;
  }

  public void setDataFim(Instant dataFim) {
    this.dataFim = dataFim;
  }

  public MatriculaStatus getStatus() {
    return status;
  }

  public void setStatus(MatriculaStatus status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
