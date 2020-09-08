package com.birozsombor4.springrestapitemplate.services;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;

public interface RegistrationService {

  User registerUser(User user);

  boolean checkRequiredParametersForLogin(UserDTO userDTO);

  String getErrorMessageForLogin(UserDTO userDTO);

  boolean checkRequiredParametersForRegister(UserDTO userDTO);

  String getErrorMessageForRegister(UserDTO userDTO);

  void verifyUser(User user);

  VerificationToken getVerificationToken(String token);

  boolean checkVerificationTokenExpired(VerificationToken verificationToken);

  void createVerificationTokenForUser(User user);
}
