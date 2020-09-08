package com.birozsombor4.springrestapitemplate.user;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@Sql(value = {"/db/test/insert_fakeUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginEndpointTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void loginUser_WithValidInput_ReturnValidResponse() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fakePassword\"\n"
            + "}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.token").exists());
  }

  @Test
  public void loginUser_WithEmptyPassword_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is required!")));
  }

  @Test
  public void loginUser_WithNullPassword_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is required!")));
  }

  @Test
  public void loginUser_WithEmptyUsername_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"\",\n"
            + "  \"password\": \"fakePassword\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is required!")));
  }

  @Test
  public void loginUser_WithNullUsername_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"password\": \"fakePassword\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is required!")));
  }

  @Test
  public void loginUser_WithEmptyUsernameAndPassword_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"\",\n"
            + "  \"password\": \"\"\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is required!; Username is required!")));
  }

  @Test
  public void loginUser_WithNullUsernameAndPassword_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is required!; Username is required!")));
  }

  @Test
  public void loginUser_WithIncorrectUsernameOrPassword_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "  \"username\": \"fakeUser\",\n"
            + "  \"password\": \"fake\"\n"
            + "}"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username or password is incorrect.")));
  }
}