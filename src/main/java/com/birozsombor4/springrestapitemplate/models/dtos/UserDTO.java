package com.birozsombor4.springrestapitemplate.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {

  private Integer id;
  private String username;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  private String email;
  private boolean verified;
  private String avatar;

  public UserDTO() {
  }

  public UserDTO(String username, String password) {
    this();
    this.username = username;
    this.password = password;
  }

  public UserDTO(Integer id, String username, String password) {
    this(username, password);
    this.id = id;
  }

  public UserDTO(int id, String username, String password, String email, String avatar) {
    this(id, username, password);
    this.email = email;
    this.avatar = avatar;
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

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}