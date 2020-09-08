package com.birozsombor4.springrestapitemplate.security;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.respositories.UserRepository;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Import(TestConfiguration.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UserDetailsServiceTest {

  @Autowired
  private BeanFactory beanFactory;
  private UserRepository userRepository;
  private UserDetailsService userDetailsService;

  @Before
  public void beforeEach() {
    userRepository = Mockito.mock(UserRepository.class);
    userDetailsService = new UserDetailsServiceImpl(userRepository);
  }

  @Test
  public void loadUserByUsername_WithExistingUsername_ReturnsValidUserDetails() {
    User fakeUser = beanFactory.getBean(User.class);
    Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(fakeUser));

    UserDetailsImpl result = (UserDetailsImpl) userDetailsService.loadUserByUsername(fakeUser.getUsername());

    Assert.assertNotNull(result);
    Assert.assertEquals(fakeUser.getId(), result.getId());
    Assert.assertEquals(fakeUser.getUsername(), result.getUsername());
    Assert.assertEquals(fakeUser.getPassword(), result.getPassword());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void loadUserByUsername_WithNotExistingUsername_ThrowUsernameNotFoundException() {
    Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    userDetailsService.loadUserByUsername("fakeUser");
  }
}