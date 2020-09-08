package com.birozsombor4.springrestapitemplate.user;

import com.birozsombor4.springrestapitemplate.avatar.AvatarService;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileLoadingException;
import com.birozsombor4.springrestapitemplate.exceptions.UserNotFoundException;
import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import com.birozsombor4.springrestapitemplate.respositories.UserRepository;
import com.birozsombor4.springrestapitemplate.services.UserService;
import com.birozsombor4.springrestapitemplate.services.UserServiceImpl;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import java.net.MalformedURLException;
import java.util.Optional;
import org.springframework.beans.factory.BeanFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(TestConfiguration.class)
@ActiveProfiles("test")
public class UserServiceTest {

  @Autowired
  private BeanFactory beanFactory;
  private UserService userService;
  private UserRepository userRepository;
  private AvatarService avatarService;

  @Before
  public void setup() {
    userRepository = Mockito.mock(UserRepository.class);
    avatarService = Mockito.mock(AvatarService.class);
    userService = new UserServiceImpl(userRepository, avatarService);
  }

  @Test
  public void convertUserDTOToUser_ReturnValidUserWithValidFields() {
    UserDTO fakeUserDTO = beanFactory.getBean(UserDTO.class);
    User result = userService.convertUserDTOToUser(fakeUserDTO);

    Assert.assertNotNull(result);
    Assert.assertEquals(fakeUserDTO.getUsername(), result.getUsername());
    Assert.assertEquals(fakeUserDTO.getPassword(), result.getPassword());
    Assert.assertEquals(fakeUserDTO.getId(), result.getId());
    Assert.assertEquals(fakeUserDTO.getAvatar(), result.getAvatar());
    Assert.assertEquals(fakeUserDTO.isVerified(), result.isVerified());
    Assert.assertEquals(fakeUserDTO.getEmail(), result.getEmail());
  }

  @Test
  public void convertUserToUserDTO_ReturnValidUserWithValidFields() {
    User fakeUser = beanFactory.getBean(User.class);
    UserDTO result = userService.convertUserToUserDTO(fakeUser);

    Assert.assertNotNull(result);
    Assert.assertEquals(fakeUser.getUsername(), result.getUsername());
    Assert.assertEquals(fakeUser.getPassword(), result.getPassword());
    Assert.assertEquals(fakeUser.getId(), result.getId());
    Assert.assertEquals(fakeUser.getAvatar(), result.getAvatar());
    Assert.assertEquals(fakeUser.isVerified(), result.isVerified());
    Assert.assertEquals(fakeUser.getEmail(), result.getEmail());
  }

  @Test
  public void getUserByUsername_WhenUserExist_ReturnUser() {
    User fakeUser = beanFactory.getBean(User.class);
    Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(fakeUser));

    User result = userService.getUserByUsername(fakeUser.getUsername());

    Assert.assertNotNull(result);
    Assert.assertEquals("fakeUser", fakeUser.getUsername());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void getUserByUsername_WhenUserNotExist_ReturnUser() {
    User fakeUser = beanFactory.getBean(User.class);
    Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    userService.getUserByUsername(fakeUser.getUsername());
  }

  @Test
  public void getUserById_WhenUserExist_ReturnUser() {
    User fakeUser = beanFactory.getBean(User.class);
    Mockito.when(userRepository.findById(Mockito.anyInt()))
        .thenReturn(Optional.of(beanFactory.getBean("fakeUser", User.class)));

    User result = userService.getUserById(fakeUser.getId());

    Assert.assertNotNull(result);
    Assert.assertEquals("fakeUser", fakeUser.getUsername());
  }

  @Test(expected = UserNotFoundException.class)
  public void getUserById_WhenUserNotExist_ReturnUser() {
    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

    userService.getUserById(1);
  }

  @Test
  public void attachAvatarToPlayer_WhenFileExists_SetPlayerAvatarField() throws MalformedURLException {
    User fakeUser = beanFactory.getBean(User.class);

    Mockito.when(avatarService.loadAvatarAsResource("newAvatar")).thenReturn(new ByteArrayResource("test".getBytes()));
    userService.attachAvatarToUser(fakeUser, "newAvatar");

    Assert.assertEquals("newAvatar", fakeUser.getAvatar());
  }

  @Test(expected = FailedFileLoadingException.class)
  public void attachAvatarToPlayer_WhenFileNotExists_ThrowException() throws MalformedURLException {
    User fakeUser = beanFactory.getBean(User.class);

    userService.attachAvatarToUser(fakeUser, "avatarWhichNotExist");
  }
}