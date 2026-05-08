package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "presencas_aula",
    uniqueConstraints = @UniqueConstraint(name = "uk_presenca_aula_aluno", columnNames = {"aula_id", "aluno_id"}))
public class PresencaAula {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aula_id", nullable = false, foreignKey = @ForeignKey(name = "fk_presenca_aula_aula"))
  private Aula aula;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_presenca_aula_aluno"))
  private Aluno aluno;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PresencaAulaStatus status = PresencaAulaStatus.PRESENTE;

  @Column(nullable = false)
  private Instant registradaEm = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Aula getAula() {
    return aula;
  }

  public void setAula(Aula aula) {
    this.aula = aula;
  }

  public Aluno getAluno() {
    return aluno;
  }

  public void setAluno(Aluno aluno) {
    this.aluno = aluno;
  }

  public PresencaAulaStatus getStatus() {
    return status;
  }

  public void setStatus(PresencaAulaStatus status) {
    this.status = status;
  }

  public Instant getRegistradaEm() {
    return registradaEm;
  }

  public void setRegistradaEm(Instant registradaEm) {
    this.registradaEm = registradaEm;
  }
}
