package com.birozsombor4.springrestapitemplate.services;

import com.birozsombor4.springrestapitemplate.exceptions.UserAlreadyVerifiedException;
import com.birozsombor4.springrestapitemplate.exceptions.VerificationTokenDoesNotExistException;
import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import com.birozsombor4.springrestapitemplate.respositories.UserRepository;
import com.birozsombor4.springrestapitemplate.respositories.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

  private UserRepository userRepository;
  private VerificationTokenRepository verificationTokenRepository;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public RegistrationServiceImpl(UserRepository userRepository,
                                 VerificationTokenRepository verificationTokenRepository,
                                 PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.verificationTokenRepository = verificationTokenRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public boolean checkRequiredParametersForLogin(UserDTO userDTO) {
    return isPasswordValid(userDTO.getPassword())
        && isUsernameValid(userDTO.getUsername());
  }

  @Override
  public String getErrorMessageForLogin(UserDTO userDTO) {
    List<String> missingParameters = new ArrayList<>();
    if (!isPasswordValid(userDTO.getPassword())) {
      missingParameters.add("Password is required!");
    }
    if (!isUsernameValid(userDTO.getUsername())) {
      missingParameters.add("Username is required!");
    }
    return String.join("; ", missingParameters);
  }

  @Override
  public boolean checkRequiredParametersForRegister(UserDTO userDTO) {
    return isPasswordValid(userDTO.getPassword())
        && isUsernameValid(userDTO.getUsername())
        && isUsernameUnique(userDTO.getUsername())
        && isEmailValid(userDTO.getEmail())
        && isEmailUnique(userDTO.getEmail());
  }

  @Override
  public String getErrorMessageForRegister(UserDTO userDTO) {
    List<String> errors = new ArrayList<>();
    if (!isPasswordValid(userDTO.getPassword())) {
      errors.add("Password is too short. Please use at least 6 characters!");
    }
    if (!isUsernameValid(userDTO.getUsername())) {
      errors.add("Username is missing!");
    }
    if (!isEmailValid(userDTO.getEmail())) {
      errors.add("Email is not correct!");
    }
    if (!isUsernameUnique(userDTO.getUsername())) {
      errors.add("User name is already taken. Please choose another one!");
    }
    if (!isEmailUnique(userDTO.getEmail())) {
      errors.add("E-mail is already taken. Please choose another one!");
    }
    return String.join("; ", errors);
  }

  @Override
  public void createVerificationTokenForUser(User user) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    user.setVerificationToken(verificationToken);
  }

  @Override
  public boolean checkVerificationTokenExpired(VerificationToken verificationToken) {
    LocalDateTime now = LocalDateTime.now();
    return now.isBefore(verificationToken.getExpiryDate());
  }

  @Override
  public VerificationToken getVerificationToken(String token) {
    Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
    return verificationTokenOptional.orElseThrow(VerificationTokenDoesNotExistException::new);
  }

  @Override
  public void verifyUser(User user) {
    if (user.isVerified()) {
      throw new UserAlreadyVerifiedException(user.getUsername());
    }
    user.setVerified(true);
    userRepository.save(user);
  }

  @Override
  public User registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  private boolean isEmailValid(String email) {
    return email != null
        && email.contains("@")
        && email.contains(".")
        && email.length() > 5;
  }

  private boolean isUsernameValid(String username) {
    return username != null && !username.isEmpty();
  }

  private boolean isPasswordValid(String password) {
    return password != null && !password.isEmpty();
  }

  private boolean isEmailUnique(String email) {
    return !userRepository.findByEmail(email).isPresent();
  }

  private boolean isUsernameUnique(String username) {
    return !userRepository.findByUsername(username).isPresent();
  }
}
