package com.dam.demo.entity;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Entity;
import com.dam.framework.annotations.GeneratedValue;
import com.dam.framework.annotations.GenerationType;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Table;

/**
 * Demo entity using UUID generation strategy.
 * <p>
 * UUID generation is database-independent and provides:
 * <ul>
 * <li>Globally unique IDs across all tables and databases</li>
 * <li>No database round-trip needed</li>
 * <li>Safe for distributed systems and replication</li>
 * <li>Works with any database (MySQL, PostgreSQL, SQL Server, etc.)</li>
 * </ul>
 * 
 * <p>
 * <b>Trade-offs:</b>
 * </p>
 * <ul>
 * <li>Larger storage size (36 bytes as string)</li>
 * <li>Non-sequential (may impact index performance)</li>
 * <li>Less human-readable than integers</li>
 * </ul>
 */
@Entity
@Table(name = "sessions")
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id; // UUID stored as String

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "token")
  private String token;

  @Column(name = "created_at")
  private java.time.LocalDateTime createdAt;

  // Constructors
  public Session() {
  }

  public Session(Long userId, String token) {
    this.userId = userId;
    this.token = token;
    this.createdAt = java.time.LocalDateTime.now();
  }

  // Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public java.time.LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "Session{id='" + id + "'" +
        ", userId=" + userId +
        ", token='" + token + "'" +
        ", createdAt=" + createdAt + "}";
  }
}
