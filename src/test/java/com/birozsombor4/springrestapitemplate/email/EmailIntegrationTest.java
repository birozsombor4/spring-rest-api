package com.birozsombor4.springrestapitemplate.email;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Import(TestConfiguration.class)
public class EmailIntegrationTest {

  @Autowired
  private EmailService emailService;
  private GreenMail smtpServer;
  @Autowired
  private BeanFactory beanFactory;

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
  public void sendVerificationEmail_SendEmailWithValidContent() throws IOException, MessagingException {
    User fakeUser = beanFactory.getBean(User.class);
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken("fakeToken");
    fakeUser.setVerificationToken(verificationToken);

    emailService.sendVerificationEmail(fakeUser);
    Assert.assertTrue(smtpServer.waitForIncomingEmail(5000, 1));

    MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
    Assert.assertEquals(1, receivedMessages.length);

    String content = (String) receivedMessages[0].getContent();
    Assert.assertTrue(content.contains("fakeUser"));
    Assert.assertTrue(content.contains("fakeToken"));
  }
}
