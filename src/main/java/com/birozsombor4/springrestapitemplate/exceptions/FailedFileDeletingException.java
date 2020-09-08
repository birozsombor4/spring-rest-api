package com.birozsombor4.springrestapitemplate.exceptions;

public class FailedFileDeletingException extends RuntimeException {

  String message;

  public FailedFileDeletingException(String message) {
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
