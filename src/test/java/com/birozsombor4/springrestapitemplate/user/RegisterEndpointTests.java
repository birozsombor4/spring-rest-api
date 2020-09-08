package com.birozsombor4.springrestapitemplate.user;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RegisterEndpointTests {

  @Autowired
  private MockMvc mockMvc;
  private GreenMail smtpServer;

  @Before
  public void setup() {
    smtpServer = new GreenMail(ServerSetupTest.SMTP);
    smtpServer.start();
  }

  @After
  public void tearDown() {
    smtpServer.stop();
  }

  @Test
  public void registerUser_WithValidUserDTO_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username", is("fakeUser")))
        .andExpect(jsonPath("$.avatar", is("default.png")))
        .andExpect(jsonPath("$.verified", is(false)))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  public void registerUser_WithEmptyUsername_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is missing!")));
  }

  @Test
  public void registerUser_WithNullUsername_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is missing!")));
  }

  @Test
  public void registerUser_WithEmptyPassword_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is too short. Please use at least 6 characters!")));
  }

  @Test
  public void registerUser_WithNullPassword_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is too short. Please use at least 6 characters!")));
  }

  @Test
  public void registerUser_WithEmptyUsernameAndPassword_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"\",\n"
            + "  \"password\": \"\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message",
            is("Password is too short. Please use at least 6 characters!; Username is missing!")));
  }

  @Test
  public void registerUser_WithNullUsernameAndPassword_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message",
            is("Password is too short. Please use at least 6 characters!; Username is missing!")));
  }

  @Test
  @Sql(value = {"/db/test/insert_fakeUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  public void registerUser_IfUsernameNotUnique_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"email@email.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("User name is already taken. Please choose another one!")));
  }

  @Test
  @Sql(value = {"/db/test/insert_fakeUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  public void registerUser_IfEmailNotUnique_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser2\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"fakeEmail@fake.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("E-mail is already taken. Please choose another one!")));
  }

  @Test
  public void registerUser_IfEmailDoesntContainAtSymbol_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"useruser.com\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Email is not correct!")));
  }

  @Test
  public void registerUser_IfEmailDoesntContainDotSymbol_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"user@usercom\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Email is not correct!")));
  }

  @Test
  public void registerUser_IfEmailIsShort_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\",\n"
            + "  \"email\": \"u@a.m\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Email is not correct!")));
  }

  @Test
  public void registerUser_IfEmailIsNull_ReturnsValidResponse() throws Exception {
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Email is not correct!")));
  }
}