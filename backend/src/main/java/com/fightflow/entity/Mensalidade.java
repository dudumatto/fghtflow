package com.fightflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "mensalidades", indexes = {
    @Index(name = "idx_mensalidade_aluno", columnList = "aluno_id"),
    @Index(name = "idx_mensalidade_vencimento", columnList = "vencimento"),
    @Index(name = "idx_mensalidade_bloqueio", columnList = "aluno_id,status,vencimento")
})
public class Mensalidade {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_mensalidade_aluno"))
  private Aluno aluno;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "plano_id", nullable = false, foreignKey = @ForeignKey(name = "fk_mensalidade_plano"))
  private Plano plano;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal valor;

  @Column(nullable = false)
  private Instant vencimento;

  private Instant dataPagamento;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MensalidadeStatus status = MensalidadeStatus.PENDENTE;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private MetodoPagamento metodoPagamento;

  @Column(length = 120)
  private String referencia;

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

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public Instant getVencimento() {
    return vencimento;
  }

  public void setVencimento(Instant vencimento) {
    this.vencimento = vencimento;
  }

  public Instant getDataPagamento() {
    return dataPagamento;
  }

  public void setDataPagamento(Instant dataPagamento) {
    this.dataPagamento = dataPagamento;
  }

  public MensalidadeStatus getStatus() {
    return status;
  }

  public void setStatus(MensalidadeStatus status) {
    this.status = status;
  }

  public MetodoPagamento getMetodoPagamento() {
    return metodoPagamento;
  }

  public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
    this.metodoPagamento = metodoPagamento;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
