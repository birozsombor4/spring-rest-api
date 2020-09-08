package com.birozsombor4.springrestapitemplate.security;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import com.birozsombor4.springrestapitemplate.utils.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Import(TestConfiguration.class)
@ActiveProfiles("test")
@Sql(value = {"/db/test/insert_fakeUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SecurityIntegrationTest {

  @Autowired
  private BeanFactory beanFactory;
  @Autowired
  MockMvc mockMvc;
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private JwtUtil jwtUtil;
  private String jwt;

  @Before
  public void setup() {
    UserDetailsImpl fakeUserDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("fakeUser");
    jwt = jwtUtil.generateToken(fakeUserDetails);
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithoutAuthentication_ReturnsValidStatus() throws Exception {
    mockMvc.perform(get("/avatar/1"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithAuthentication_ReturnsValidStatus() throws Exception {
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk());
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithoutBearerPrefix_ReturnsValidStatusAndMessage() throws Exception {
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", jwt))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Invalid Authorization or missing JWT token.")));
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithWrongHeader_ReturnsValidStatusAndMessage() throws Exception {
    mockMvc.perform(get("/avatar/1")
        .header("Authorizationasd", "Bearer " + jwt))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Invalid Authorization or missing JWT token.")));
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithInvalidJwt_ReturnsValidStatusAndMessage() throws Exception {
    jwt += "asd";
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Invalid JWT format.")));
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithMissingUsername_ReturnsValidStatusAndMessage() throws Exception {
    User fakeUser = beanFactory.getBean(User.class);
    fakeUser.setUsername(null);
    UserDetailsImpl fakeUserDetails = new UserDetailsImpl(fakeUser);
    String jwtWithMissingUsername = jwtUtil.generateToken(fakeUserDetails);
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + jwtWithMissingUsername))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is missing from JWT.")));
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithNotExistingUsername_ReturnsValidResponse() throws Exception {
    User fakeUser = beanFactory.getBean(User.class);
    fakeUser.setUsername("");
    UserDetailsImpl fakeUserDetails = new UserDetailsImpl(fakeUser);
    String jwtWithMissingUsername = jwtUtil.generateToken(fakeUserDetails);
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + jwtWithMissingUsername))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username not found.")));
  }

  @Test
  public void sendRequestForAuthenticatedEndpoint_WithExpiredToken_ReturnsValidStatusAndMessage() throws Exception {
    String expiredJwt = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsImV4cCI6MTU5MDgwMjk1MiwiaWF0IjoxNTkwNzY2OTUyfQ"
        + ".TvUo_-6GalD6rQm10eACxLQ4CsaMXOhXUVE_3d36UKs";
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + expiredJwt))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Expired JWT.")));
  }
}