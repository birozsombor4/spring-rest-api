package com.birozsombor4.springrestapitemplate.testconfiguration;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
public class TestConfiguration {

  @Bean(name = {"fakeUser"})
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  User getUser() {
    return new User(1, "fakeUser", "fakePassword", "fakeEmail@fake.com");
  }

  @Bean(name = {"fakeUserDTO"})
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  UserDTO getUserDTO() {
    return new UserDTO(1, "fakeUserDTO", "fakePassword", "fakeEmail@fake.com", "default.png");
  }
}