package com.birozsombor4.springrestapitemplate.exceptions;

public class FailedFileLoadingException extends RuntimeException {

  private String message;

  public FailedFileLoadingException(String message) {
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
