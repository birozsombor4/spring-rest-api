package com.birozsombor4.springrestapitemplate.security;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.testconfiguration.TestConfiguration;
import com.birozsombor4.springrestapitemplate.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(TestConfiguration.class)
@ActiveProfiles("test")
public class JwtUtilTest {
  //region preGeneratedTokens
  /*
     Tested on https://jwt.io/ with following details
     preGeneratedToken (expired)
     alg: HS256
     sub: fakeUser
     user_id: 1
     exp: 1594161231
     iat: 1594125231
     secret base64 encoded: test

     preGeneratedNotExpiredToken
     alg: HS256
     sub: fakeUser
     exp: 15941612310000
     iat: 1594161231
     secret base64 encoded: test
  */
  //endregion

  @Autowired
  private BeanFactory beanFactory;
  private JwtUtil jwtUtil;

  @Before
  public void setup() {
    this.jwtUtil = new JwtUtil();
  }

  @Test
  public void generateToken_ReturnsNotNullAndValidJwtFormat() {
    User fakePlayer = beanFactory.getBean(User.class);
    UserDetailsImpl fakeUserDetails = new UserDetailsImpl(fakePlayer);

    String jwt = jwtUtil.generateToken(fakeUserDetails);

    int dotCounter = 0;
    for (char c : jwt.toCharArray()) {
      if (c == '.') {
        dotCounter++;
      }
    }
    Assert.assertNotNull(jwt);
    Assert.assertEquals(2, dotCounter);
  }

  @Test
  public void extractClaim_WithPreGeneratedTokenAndGetSubject_ReturnsValidSubject() {
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";

    String subject = jwtUtil.extractClaim(preGeneratedToken, Claims::getSubject);

    Assert.assertNotNull(subject);
    Assert.assertEquals("fakeUser", subject);
  }

  @Test
  public void extractClaim_WithPreGeneratedTokenAndGetIssuedAt_ReturnsValidSubject() {
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";

    Date issuedAt = jwtUtil.extractClaim(preGeneratedToken, Claims::getIssuedAt);

    Assert.assertNotNull(issuedAt);
    Assert.assertEquals(1594125231000L, issuedAt.getTime());
  }

  @Test
  public void extractClaim_WithPreGeneratedTokenAndGetExpiration_ReturnsValidSubject() {
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";

    Date expiration = jwtUtil.extractClaim(preGeneratedToken, Claims::getExpiration);

    Assert.assertNotNull(expiration);
    Assert.assertEquals(1594161231000L, expiration.getTime());
  }


  @Test
  public void extractUsername_WithPreGeneratedToken_ReturnsValidUsername() {
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";
    ;

    String username = jwtUtil.extractUsername(preGeneratedToken);

    Assert.assertEquals("fakeUser", username);
  }

  @Test
  public void extractExpirationDate_WithPreGeneratedToken_ReturnsNotNull() {
    Date expectedDate = new Date(1594161231000L);
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";

    Date expiration = jwtUtil.extractExpiration(preGeneratedToken);
    Assert.assertEquals(expectedDate, expiration);
  }

  @Test
  public void validateToken_WithExpiredTokenAndValidUsername_ReturnsFalse() {
    User fakeUser = beanFactory.getBean(User.class);
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";

    boolean result = jwtUtil.validateToken(preGeneratedToken, new UserDetailsImpl(fakeUser));

    Assert.assertFalse(result);
  }

  @Test
  public void validateToken_WithExpiredTokenAndNotValidUsername_ReturnsFalse() {
    User fakeUser = beanFactory.getBean(User.class);
    fakeUser.setUsername("anotherFakeUser");
    String preGeneratedToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMSwiaWF0IjoxNTk0MTI1MjMxfQ"
        + ".49eszPLHsu9c2NfrO-BaNCwnvH5EcmjFGVlU8lhwfAk";

    boolean result = jwtUtil.validateToken(preGeneratedToken, new UserDetailsImpl(fakeUser));

    Assert.assertFalse(result);
  }

  @Test
  public void validateToken_WithNotExpiredTokenAndValidUsername_ReturnsTrue() {
    User fakeUser = beanFactory.getBean(User.class);
    String preGeneratedNotExpiredToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMTAwMDAsImlhdCI6MTU5NDEyNTIzMX0"
        + ".mV9NLDSUbakDIYQ38RfZ7K2558rBO_3gXEJrnuUCcGs";

    boolean result = jwtUtil.validateToken(preGeneratedNotExpiredToken, new UserDetailsImpl(fakeUser));

    Assert.assertTrue(result);
  }

  @Test
  public void validateToken_WithNotExpiredTokenAndNotValidUsername_ReturnsFalse() {
    User fakeUser = beanFactory.getBean(User.class);
    fakeUser.setUsername("anotherFakeUser");
    String preGeneratedNotExpiredToken = "eyJhbGciOiJIUzI1NiJ9"
        + ".eyJzdWIiOiJmYWtlVXNlciIsInVzZXJfaWQiOjEsImV4cCI6MTU5NDE2MTIzMTAwMDAsImlhdCI6MTU5NDEyNTIzMX0"
        + ".mV9NLDSUbakDIYQ38RfZ7K2558rBO_3gXEJrnuUCcGs";

    boolean result = jwtUtil.validateToken(preGeneratedNotExpiredToken, new UserDetailsImpl(fakeUser));

    Assert.assertFalse(result);
  }
}