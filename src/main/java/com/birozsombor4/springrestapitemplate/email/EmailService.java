package com.birozsombor4.springrestapitemplate.email;

import com.birozsombor4.springrestapitemplate.models.daos.User;

public interface EmailService {
  void sendVerificationEmail(User user);
}
