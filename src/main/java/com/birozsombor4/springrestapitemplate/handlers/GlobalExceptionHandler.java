package com.birozsombor4.springrestapitemplate.handlers;

import com.birozsombor4.springrestapitemplate.exceptions.FailedDirectoryCreatingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileDeletingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileLoadingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileSavingException;
import com.birozsombor4.springrestapitemplate.exceptions.InvalidRegisterOrLoginParameterException;
import com.birozsombor4.springrestapitemplate.exceptions.NotAllowedActionException;
import com.birozsombor4.springrestapitemplate.exceptions.UnsupportedContentTypeException;
import com.birozsombor4.springrestapitemplate.exceptions.UnsupportedFileNameException;
import com.birozsombor4.springrestapitemplate.exceptions.UserAlreadyVerifiedException;
import com.birozsombor4.springrestapitemplate.exceptions.UserNotFoundException;
import com.birozsombor4.springrestapitemplate.exceptions.VerificationTokenDoesNotExistException;
import com.birozsombor4.springrestapitemplate.models.dtos.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity handleMissingRegisterOrLoginParameterException(InvalidRegisterOrLoginParameterException e) {
    return ResponseEntity.badRequest().body(new ErrorDTO("error", e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleBadCredentialsException(BadCredentialsException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorDTO("error", "Username or password is incorrect."));
  }

  @ExceptionHandler
  public ResponseEntity handleUserDoesNotExist(UsernameNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorDTO("error", "User does not exist with given id/name: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleUserDoesNotExist(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorDTO("error", "User does not exist with given id/name: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleVerificationTokenDoesNotExistException(VerificationTokenDoesNotExistException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorDTO("error", "Verification token does not exist."));
  }

  @ExceptionHandler
  public ResponseEntity handleDisabledException(DisabledException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorDTO("error", "User is not verified."));
  }

  @ExceptionHandler
  public ResponseEntity handleUserHasAlreadyVerifiedException(UserAlreadyVerifiedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorDTO("error", e.getMessage() + " has already verified."));
  }

  @ExceptionHandler
  public ResponseEntity handleUnsupportedContentType(UnsupportedContentTypeException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorDTO("error", "Unsupported Content-Type: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleUnsupportedFileNameException(UnsupportedFileNameException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorDTO("error", "Unsupported filename: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleFailedFileSavingException(FailedFileSavingException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("error", "Something went wrong with file saving: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleFailedFileLoadingException(FailedFileLoadingException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("error", "Something went wrong with file loading: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleFailedFileDeletingException(FailedFileDeletingException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("error", "Something went wrong with file deleting: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleFailedDirectoryCreatingException(FailedDirectoryCreatingException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("error", "Something went wrong with creating directory: " + e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity handleSizeLimitExceededException(MultipartException e) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body(new ErrorDTO("error", "Maximum image size: 2MB"));
  }

  @ExceptionHandler
  public ResponseEntity handleNotAllowedActionException(NotAllowedActionException e) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(new ErrorDTO("error", e.getMessage()));
  }
}