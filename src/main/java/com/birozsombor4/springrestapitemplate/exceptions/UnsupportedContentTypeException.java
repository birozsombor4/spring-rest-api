package com.birozsombor4.springrestapitemplate.exceptions;

public class UnsupportedContentTypeException extends RuntimeException {

  private String message;

  public UnsupportedContentTypeException(String message) {
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
