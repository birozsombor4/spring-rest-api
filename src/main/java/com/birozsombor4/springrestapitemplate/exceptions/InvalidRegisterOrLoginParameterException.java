package com.birozsombor4.springrestapitemplate.exceptions;

public class InvalidRegisterOrLoginParameterException extends RuntimeException {

  private String message;

  public InvalidRegisterOrLoginParameterException(String message) {
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