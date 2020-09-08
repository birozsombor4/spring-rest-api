package com.birozsombor4.springrestapitemplate.exceptions;

public class UnsupportedFileNameException extends RuntimeException {

  private String message;

  public UnsupportedFileNameException(String message) {
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
