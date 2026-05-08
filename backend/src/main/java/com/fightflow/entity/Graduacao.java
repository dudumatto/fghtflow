package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "graduacoes", indexes = {
    @Index(name = "idx_graduacao_aluno_data", columnList = "aluno_id,dataGraduacao")
})
public class Graduacao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_graduacao_aluno"))
  private Aluno aluno;

  @Column(nullable = false, length = 30)
  private String faixa;

  @Column(nullable = false)
  private int grau;

  @Column(nullable = false)
  private Instant dataGraduacao;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "professor_usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_graduacao_professor"))
  private Usuario professorResponsavel;

  @Column(length = 1000)
  private String observacao;

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

  public String getFaixa() {
    return faixa;
  }

  public void setFaixa(String faixa) {
    this.faixa = faixa;
  }

  @Transient
  public Faixa getFaixaEnum() {
    if (faixa == null) return null;
    return Faixa.valueOf(faixa);
  }

  public void setFaixaEnum(Faixa faixa) {
    this.faixa = faixa == null ? null : faixa.name();
  }

  public int getGrau() {
    return grau;
  }

  public void setGrau(int grau) {
    this.grau = grau;
  }

  public Instant getDataGraduacao() {
    return dataGraduacao;
  }

  public void setDataGraduacao(Instant dataGraduacao) {
    this.dataGraduacao = dataGraduacao;
  }

  public Usuario getProfessorResponsavel() {
    return professorResponsavel;
  }

  public void setProfessorResponsavel(Usuario professorResponsavel) {
    this.professorResponsavel = professorResponsavel;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String observacao) {
    this.observacao = observacao;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
