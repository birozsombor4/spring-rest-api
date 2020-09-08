package com.birozsombor4.springrestapitemplate.exceptions;

public class UserAlreadyVerifiedException extends RuntimeException {

  String message;

  public UserAlreadyVerifiedException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
