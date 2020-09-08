package com.birozsombor4.springrestapitemplate.email;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {
  private EmailConfig emailConfig;
  private TemplateEngine templateEngine;
  private JavaMailSender mailSender;

  @Autowired
  public EmailServiceImpl(EmailConfig emailConfig, TemplateEngine templateEngine) {
    this.emailConfig = emailConfig;
    this.templateEngine = templateEngine;
    this.mailSender = initMailSender();
  }

  @Override
  public void sendVerificationEmail(User user) {
    MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom("admin@admin.com");
      messageHelper.setTo(user.getEmail());
      messageHelper.setSubject("App Verification");
      String content = buildVerificationTemplate(user);
      messageHelper.setText(content, true);
    };
    mailSender.send(mimeMessagePreparator);
  }

  private String buildVerificationTemplate(User user) {
    Context context = new Context();
    context.setVariable("username", user.getUsername());
    context.setVariable("verificationToken", user.getVerificationToken().getToken());
    return templateEngine.process("verification-email", context);
  }

  private JavaMailSender initMailSender() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(emailConfig.getHost());
    javaMailSender.setPort(emailConfig.getPort());
    javaMailSender.setUsername(emailConfig.getUsername());
    javaMailSender.setPassword(emailConfig.getPassword());
    return javaMailSender;
  }
}
