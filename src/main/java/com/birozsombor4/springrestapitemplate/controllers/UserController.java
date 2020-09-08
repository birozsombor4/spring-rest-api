package com.birozsombor4.springrestapitemplate.controllers;

import com.birozsombor4.springrestapitemplate.avatar.AvatarService;
import com.birozsombor4.springrestapitemplate.email.EmailService;
import com.birozsombor4.springrestapitemplate.exceptions.InvalidRegisterOrLoginParameterException;
import com.birozsombor4.springrestapitemplate.exceptions.NotAllowedActionException;
import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import com.birozsombor4.springrestapitemplate.models.dtos.AuthenticationResponseDTO;
import com.birozsombor4.springrestapitemplate.models.dtos.MessageDTO;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import com.birozsombor4.springrestapitemplate.security.UserDetailsImpl;
import com.birozsombor4.springrestapitemplate.services.RegistrationService;
import com.birozsombor4.springrestapitemplate.services.UserService;
import com.birozsombor4.springrestapitemplate.utils.JwtUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

  private UserService userService;
  private JwtUtil jwtUtil;
  private AuthenticationManager authenticationManager;
  private UserDetailsService userDetailsService;
  private EmailService emailService;
  private AvatarService avatarService;
  private RegistrationService registrationService;


  @Autowired
  public UserController(UserService userService,
                        JwtUtil jwtUtil,
                        AuthenticationManager authenticationManager,
                        UserDetailsService userDetailsService,
                        EmailService emailService,
                        AvatarService avatarService,
                        RegistrationService registrationService) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.emailService = emailService;
    this.avatarService = avatarService;
    this.registrationService = registrationService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) throws MalformedURLException {
    if (!registrationService.checkRequiredParametersForRegister(userDTO)) {
      throw new InvalidRegisterOrLoginParameterException(registrationService.getErrorMessageForRegister(userDTO));
    }
    User user = userService.convertUserDTOToUser(userDTO);
    userService.attachAvatarToUser(user, "default.png");
    registrationService.createVerificationTokenForUser(user);
    emailService.sendVerificationEmail(user);
    UserDTO responseUserDTO = userService.convertUserToUserDTO(registrationService.registerUser(user));
    return ResponseEntity.ok(responseUserDTO);
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) throws BadCredentialsException {
    if (!registrationService.checkRequiredParametersForLogin(userDTO)) {
      throw new InvalidRegisterOrLoginParameterException(registrationService.getErrorMessageForLogin(userDTO));
    }
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
    );
    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userDTO.getUsername());
    String jwt = jwtUtil.generateToken(userDetails);
    return ResponseEntity.ok(new AuthenticationResponseDTO(jwt));
  }

  @GetMapping("/verify")
  public ResponseEntity<?> verifyUser(@RequestParam String token) {
    VerificationToken verificationToken = registrationService.getVerificationToken(token);
    if (!registrationService.checkVerificationTokenExpired(verificationToken)) {
      User user = verificationToken.getUser();
      registrationService.createVerificationTokenForUser(user);
      user.updateVerificationTokenId();
      userService.updateUser(user);
      emailService.sendVerificationEmail(user);
      String message = "Email verification link has expired. We'll send another for your email: " + user.getEmail();
      return ResponseEntity.ok(new MessageDTO("ok", message));
    }
    registrationService.verifyUser(verificationToken.getUser());
    return ResponseEntity.ok(new MessageDTO("ok", verificationToken.getUser().getUsername() + " has verified."));
  }

  @PostMapping("/avatar/{userId}")
  public ResponseEntity<?> uploadAvatarAndAttachToUser(@PathVariable Integer userId,
                                                       @RequestParam MultipartFile image) throws IOException {
    String authenticatedUsername = userService.getAuthenticatedUsername();
    User user = userService.getUserById(userId);
    if (!user.getUsername().equals(authenticatedUsername)) {
      throw new NotAllowedActionException("User id: " + userId + " doesn't belongs to user: " + authenticatedUsername);
    }
    String newImageName = avatarService.renameAndStore(image, userId);
    userService.attachAvatarToUser(user, newImageName);
    userService.updateUser(user);
    return ResponseEntity.ok(new MessageDTO("ok", "Avatar has updated for user: " + user.getUsername()));
  }

  @GetMapping("/avatar/{userId}")
  public ResponseEntity<?> getAvatarByUserIdAsResource(@PathVariable Integer userId) throws MalformedURLException {
    User user = userService.getUserById(userId);
    Resource file = avatarService.loadAvatarAsResource(user.getAvatar());
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=" + file.getFilename());
    headers.add(HttpHeaders.CONTENT_TYPE, avatarService.getAvatarContentType(file.getFilename()));
    return ResponseEntity.ok().headers(headers).body(file);
  }
}