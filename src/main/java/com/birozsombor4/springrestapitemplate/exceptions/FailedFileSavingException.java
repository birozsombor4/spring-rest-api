package com.birozsombor4.springrestapitemplate.exceptions;

public class FailedFileSavingException extends RuntimeException {

  private String message;

  public FailedFileSavingException(String message) {
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
