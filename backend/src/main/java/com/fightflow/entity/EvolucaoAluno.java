package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "evolucoes_aluno", indexes = {
    @Index(name = "idx_evolucao_aluno_data", columnList = "aluno_id,data")
})
public class EvolucaoAluno {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_evolucao_aluno"))
  private Aluno aluno;

  @Column(nullable = false, length = 60)
  private String tipo;

  @Column(nullable = false, length = 1000)
  private String descricao;

  @Column(nullable = false)
  private Instant data;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "professor_usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_evolucao_professor"))
  private Usuario professorResponsavel;

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

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public Instant getData() {
    return data;
  }

  public void setData(Instant data) {
    this.data = data;
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
}
