package com.fightflow.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "documentos")
public class Documento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_usuario_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_documento_owner"))
  private Usuario owner;

  @Column(nullable = false, length = 240)
  private String originalName;

  @Column(nullable = false, length = 80)
  private String storedName;

  @Column(nullable = false, length = 120)
  private String mimeType;

  @Column(nullable = false)
  private long sizeBytes;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getOwner() {
    return owner;
  }

  public void setOwner(Usuario owner) {
    this.owner = owner;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public String getStoredName() {
    return storedName;
  }

  public void setStoredName(String storedName) {
    this.storedName = storedName;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public long getSizeBytes() {
    return sizeBytes;
  }

  public void setSizeBytes(long sizeBytes) {
    this.sizeBytes = sizeBytes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

