package com.birozsombor4.springrestapitemplate.models.daos;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String username;
  private String password;
  private String email;
  private boolean verified;
  private String avatar;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
  private VerificationToken verificationToken;

  public User() {
  }

  public User(String username, String password) {
    this();
    this.username = username;
    this.password = password;
  }

  public User(Integer id, String username, String password) {
    this(username, password);
    this.id = id;
  }

  public User(int id, String username, String password, String email) {
    this(id, username, password);
    this.email = email;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public VerificationToken getVerificationToken() {
    return verificationToken;
  }

  public void setVerificationToken(VerificationToken verificationToken) {
    this.verificationToken = verificationToken;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public void updateVerificationTokenId() {
    verificationToken.setId(id);
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}