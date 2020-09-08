package com.birozsombor4.springrestapitemplate.email;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import com.birozsombor4.springrestapitemplate.respositories.VerificationTokenRepository;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.mail.internet.MimeMessage;
import org.junit.After;
import org.junit.Assert;
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

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class VerifyEndpointTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private VerificationTokenRepository verificationTokenRepository;
  private GreenMail smtpServer;
  private String verificationToken;

  @Before
  public void setup() throws Exception {
    smtpServer = new GreenMail(ServerSetupTest.SMTP);
    smtpServer.start();
    mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\n"
            + "\"username\": \"fakeUsername\",\n"
            + "\"email\": \"fakeEmail@fake.com\",\n"
            + "\"password\": \"fakePassword\",\n"
            + "\"kingdomname\": \"fakeKingdomName\"\n"
            + "}"));
    String verificationEmailContent = (String) smtpServer.getReceivedMessages()[0].getContent();
    int tokenStartIndex = verificationEmailContent.indexOf("?token=") + 7;
    verificationToken = verificationEmailContent
        .substring(tokenStartIndex, tokenStartIndex + UUID.randomUUID().toString().length());
  }

  @After
  public void tearDown() {
    smtpServer.stop();
  }

  @Test
  public void verifyUser_WithInvalidVerificationToken_ReturnsValidStatusAndError() throws Exception {
    mockMvc.perform(get("/verify?token=" + "test"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Verification token does not exist.")));
  }

  @Test
  public void verifyUser_WithValidVerificationTokenAfterRegister_ReturnsValidStatus() throws Exception {
    mockMvc.perform(get("/verify?token=" + verificationToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("ok")))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  public void verifyUser_AfterSuccessfullyValidation_ReturnsValidStatusAndError() throws Exception {
    mockMvc.perform(get("/verify?token=" + verificationToken))
        .andExpect(status().isOk());
    mockMvc.perform(get("/verify?token=" + verificationToken))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message",
            is("fakeUsername has already verified.")));
  }

  @Test
  public void verifyUser_WithValidExpiredToken_ReturnsValidStatusAndSendANewToken() throws Exception {
    VerificationToken token = verificationTokenRepository.findByToken(verificationToken).get();
    token.setExpiryDate(LocalDateTime.now().minusHours(24));
    verificationTokenRepository.save(token);

    mockMvc.perform(get("/verify?token=" + this.verificationToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("ok")))
        .andExpect(jsonPath("$.message", is("Email verification link has expired. We'll send "
            + "another for your email: fakeEmail@fake.com")));

    MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
    Assert.assertEquals(2, receivedMessages.length);

    String verificationEmailContent = (String) smtpServer.getReceivedMessages()[1].getContent();
    int tokenStartIndex = verificationEmailContent.indexOf("?token=") + 7;
    String resentToken = verificationEmailContent
        .substring(tokenStartIndex, tokenStartIndex + UUID.randomUUID().toString().length());
    Assert.assertFalse(verificationToken.equals(resentToken));
  }
}