package com.birozsombor4.springrestapitemplate.models.daos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
  @Transient
  private static final int EXPIRATION = 24;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String token;
  private LocalDateTime expiryDate;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private User user;

  public VerificationToken() {
    this.token = generateRandomToken();
    this.expiryDate = calculateExpiryDate();
  }

  private LocalDateTime calculateExpiryDate() {
    return LocalDateTime.now().plusHours(EXPIRATION);
  }

  private String generateRandomToken() {
    return UUID.randomUUID().toString();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
