package com.volleyfitness.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "plans")
public class Plan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private LocalDate planDate;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String planJson;

  @Column(nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  protected Plan() {}

  public Plan(User user, LocalDate planDate, String planJson) {
    this.user = user;
    this.planDate = planDate;
    this.planJson = planJson;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public LocalDate getPlanDate() {
    return planDate;
  }

  public String getPlanJson() {
    return planJson;
  }

  public void setPlanJson(String planJson) {
    this.planJson = planJson;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
