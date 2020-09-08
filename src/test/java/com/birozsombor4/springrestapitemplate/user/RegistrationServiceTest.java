package com.birozsombor4.springrestapitemplate.user;

import com.birozsombor4.springrestapitemplate.exceptions.UserAlreadyVerifiedException;
import com.birozsombor4.springrestapitemplate.exceptions.VerificationTokenDoesNotExistException;
import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import com.birozsombor4.springrestapitemplate.respositories.UserRepository;
import com.birozsombor4.springrestapitemplate.respositories.VerificationTokenRepository;
import com.birozsombor4.springrestapitemplate.services.RegistrationService;
import com.birozsombor4.springrestapitemplate.services.RegistrationServiceImpl;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(TestConfiguration.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

  @Autowired
  private BeanFactory beanFactory;
  private UserRepository userRepository;
  private VerificationTokenRepository verificationTokenRepository;
  private PasswordEncoder passwordEncoder;
  private RegistrationService registrationService;


  @Before
  public void setup() {
    userRepository = Mockito.mock(UserRepository.class);
    verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
    passwordEncoder = Mockito.mock(PasswordEncoder.class);
    registrationService = new RegistrationServiceImpl(userRepository, verificationTokenRepository, passwordEncoder);
  }

  @Test
  public void checkRequiredParametersForLogin_WhenPasswordNull_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);

    boolean result = registrationService.checkRequiredParametersForLogin(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForLogin_WhenPasswordEmpty_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword("");

    boolean result = registrationService.checkRequiredParametersForLogin(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForLogin_WhenUsernameNull_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername(null);

    boolean result = registrationService.checkRequiredParametersForLogin(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForLogin_WithUsernameEmpty_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername("");

    boolean result = registrationService.checkRequiredParametersForLogin(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegisterAndLogin_WithValidUsernameAndPassword_ReturnTrue() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);

    boolean result = registrationService.checkRequiredParametersForLogin(fakeUserDTO);

    Assert.assertTrue(result);
  }

  @Test
  public void getErrorMessageForLogin_WhenPasswordNull_ReturnValidString() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);

    String result = registrationService.getErrorMessageForLogin(fakeUserDTO);

    Assert.assertEquals("Password is required!", result);
  }

  @Test
  public void getErrorMessageForLogin_WhenPasswordEmpty_ReturnValidString() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword("");

    String result = registrationService.getErrorMessageForLogin(fakeUserDTO);

    Assert.assertEquals("Password is required!", result);
  }

  @Test
  public void getErrorMessageForLogin_WhenUsernameNull_ReturnValidString() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername(null);

    String result = registrationService.getErrorMessageForLogin(fakeUserDTO);

    Assert.assertEquals("Username is required!", result);
  }

  @Test
  public void getErrorMessageForLogin_WhenUsernameEmpty_ReturnValidString() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername("");

    String result = registrationService.getErrorMessageForLogin(fakeUserDTO);

    Assert.assertEquals("Username is required!", result);
  }

  @Test
  public void getErrorMessageForLogin_WithInvalidUsernameAndPassword_ReturnValidString() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);
    fakeUserDTO.setUsername(null);

    String result = registrationService.getErrorMessageForLogin(fakeUserDTO);

    Assert.assertEquals("Password is required!; Username is required!", result);
  }

  @Test
  public void getErrorMessageForLogin_WithValidUsernameAndPassword_ReturnEmptyString() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);

    String result = registrationService.getErrorMessageForLogin(fakeUserDTO);

    Assert.assertEquals("", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenPasswordIsNull_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Password is too short. Please use at least 6 characters!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenPasswordIsEmpty_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword("");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Password is too short. Please use at least 6 characters!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenEmailIsNull_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail(null);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Email is not correct!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenEmailNotContainsAtSymbol_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("fakeEmailfake.com");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Email is not correct!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenEmailNotContainsDotSymbol_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("fakeEmail@fakecom");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Email is not correct!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenEmailIsNotEnoughLong_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("f@a.m");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Email is not correct!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenEmailIsNotUnique_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("fakeEmailfake.com");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Email is not correct!; E-mail is already taken. Please choose another one!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenUsernameIsNull_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername(null);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);
    Assert.assertEquals("Username is missing!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenUsernameIsEmpty_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername("");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Username is missing!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenUsernameIsNotUnique_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("User name is already taken. Please choose another one!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenUserDTONotValid_ReturnProperErrorMessage() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);
    fakeUserDTO.setEmail("mail");
    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("Password is too short. Please use at least 6 characters!; Email is not correct!; User name "
        + "is already taken. Please choose another one!; E-mail is already taken. Please choose another one!", result);
  }

  @Test
  public void getErrorMessageForRegister_WhenUserDTOIsValid_ReturnEmptyList() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    String result = registrationService.getErrorMessageForRegister(fakeUserDTO);

    Assert.assertEquals("", result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenPasswordIsNull_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenPasswordIsEmpty_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword("");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenEmailIsNull_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail(null);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenEmailNotContainsAtSymbol_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("fakeEmailfake.com");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenEmailNotContainsDotSymbol_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("fakeEmail@fakecom");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenEmailIsNotEnoughLong_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("f@a.m");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenEmailIsNotUnique_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setEmail("fakeEmailfake.com");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenUsernameIsNull_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername(null);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenUsernameIsEmpty_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setUsername("");

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenUsernameIsNotUnique_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenUserDTONotValid_ReturnFalse() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    fakeUserDTO.setPassword(null);
    fakeUserDTO.setEmail("mail");
    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertFalse(result);
  }

  @Test
  public void checkRequiredParametersForRegister_WhenUserDTOIsValid_ReturnTrue() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);

    Mockito.when(userRepository.findByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userRepository.findByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());

    boolean result = registrationService.checkRequiredParametersForRegister(fakeUserDTO);

    Assert.assertTrue(result);
  }



  @Test
  public void register_ReturnUserWithEncodedPassword() {
    User fakeUser = beanFactory.getBean("fakeUser", User.class);

    Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(fakeUser);

    registrationService.registerUser(fakeUser);

    Assert.assertEquals("encoded", fakeUser.getPassword());
  }

  @Test
  public void createVerificationTokenForUser_SetVerificationTokenField() {
    User fakeUser = beanFactory.getBean("fakeUser", User.class);
    Assert.assertNull(fakeUser.getVerificationToken());

    registrationService.createVerificationTokenForUser(fakeUser);

    Assert.assertNotNull(fakeUser.getVerificationToken());
    Assert.assertNotNull(fakeUser.getVerificationToken().getUser());
    Assert.assertEquals(fakeUser, fakeUser.getVerificationToken().getUser());
  }

  @Test
  public void verifyUser_WhenUserIsNotVerifiedYet_SetVerifiedFieldToTrue() {
    User fakeUser = beanFactory.getBean("fakeUser", User.class);
    Assert.assertFalse(fakeUser.isVerified());

    registrationService.verifyUser(fakeUser);

    Assert.assertTrue(fakeUser.isVerified());
  }

  @Test(expected = UserAlreadyVerifiedException.class)
  public void verifyUser_WhenUserIsAlreadyVerified_ThrowException() {
    User fakeUser = beanFactory.getBean("fakeUser", User.class);
    fakeUser.setVerified(true);

    registrationService.verifyUser(fakeUser);
  }

  @Test
  public void getVerificationToken_WhenTokenIsExist_ReturnToken() {
    Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString()))
        .thenReturn(Optional.of(new VerificationToken()));

    VerificationToken result = registrationService.getVerificationToken(UUID.randomUUID().toString());

    Assert.assertNotNull(result);
  }

  @Test(expected = VerificationTokenDoesNotExistException.class)
  public void getVerificationToken_WhenTokenIsNotExist_ThrowException() {
    Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString()))
        .thenReturn(Optional.empty());

    registrationService.getVerificationToken(UUID.randomUUID().toString());
  }

  @Test
  public void checkVerificationTokenExpired_WhenTokenIsNotExpired_ReturnTrue() {
    VerificationToken notExpiredToken = new VerificationToken();

    boolean result = registrationService.checkVerificationTokenExpired(notExpiredToken);

    Assert.assertTrue(result);
  }

  @Test
  public void checkVerificationTokenExpired_WhenTokenIsExpired_ReturnFalse() {
    VerificationToken expiredToken = new VerificationToken();
    expiredToken.setExpiryDate(LocalDateTime.now().minusHours(48));

    boolean result = registrationService.checkVerificationTokenExpired(expiredToken);

    Assert.assertFalse(result);
  }
}
