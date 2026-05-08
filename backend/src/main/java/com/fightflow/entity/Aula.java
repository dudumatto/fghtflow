package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "aulas", indexes = {
    @Index(name = "idx_aula_academia_inicio", columnList = "academia_id,dataHoraInicio")
})
public class Aula {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academia_id", nullable = false, foreignKey = @ForeignKey(name = "fk_aula_academia"))
  private Academia academia;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "professor_usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_aula_professor"))
  private Usuario professor;

  @Column(nullable = false, length = 120)
  private String titulo;

  @Column(length = 1000)
  private String descricao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AulaTipo tipo = AulaTipo.COLETIVA;

  @Column(nullable = false)
  private Instant dataHoraInicio;

  @Column(nullable = false)
  private Instant dataHoraFim;

  private Integer capacidade;

  @Column(nullable = false)
  private boolean ativa = true;

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

  public Usuario getProfessor() {
    return professor;
  }

  public void setProfessor(Usuario professor) {
    this.professor = professor;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public AulaTipo getTipo() {
    return tipo;
  }

  public void setTipo(AulaTipo tipo) {
    this.tipo = tipo;
  }

  public Instant getDataHoraInicio() {
    return dataHoraInicio;
  }

  public void setDataHoraInicio(Instant dataHoraInicio) {
    this.dataHoraInicio = dataHoraInicio;
  }

  public Instant getDataHoraFim() {
    return dataHoraFim;
  }

  public void setDataHoraFim(Instant dataHoraFim) {
    this.dataHoraFim = dataHoraFim;
  }

  public Integer getCapacidade() {
    return capacidade;
  }

  public void setCapacidade(Integer capacidade) {
    this.capacidade = capacidade;
  }

  public boolean isAtiva() {
    return ativa;
  }

  public void setAtiva(boolean ativa) {
    this.ativa = ativa;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

