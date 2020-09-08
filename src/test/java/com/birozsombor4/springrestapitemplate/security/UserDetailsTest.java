package com.birozsombor4.springrestapitemplate.security;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Import(TestConfiguration.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UserDetailsTest {

  @Autowired
  private BeanFactory beanFactory;

  @Test
  public void userDetailsImpl_WithPlayer_returnsExpectedFields() {
    User fakeUser = beanFactory.getBean(User.class);

    UserDetailsImpl result = new UserDetailsImpl(fakeUser);

    Assert.assertEquals(fakeUser.getId(), result.getId());
    Assert.assertEquals(fakeUser.getUsername(), result.getUsername());
    Assert.assertEquals(fakeUser.getPassword(), result.getPassword());
    Assert.assertEquals(0, result.getAuthorities().size());
    Assert.assertTrue(result.isAccountNonExpired());
    Assert.assertTrue(result.isAccountNonLocked());
    Assert.assertTrue(result.isCredentialsNonExpired());
    Assert.assertFalse(result.isEnabled());
  }
}