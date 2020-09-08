package com.birozsombor4.springrestapitemplate.utils;

import com.birozsombor4.springrestapitemplate.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private String secretKey = System.getenv("SECRET_KEY");

  //region CREATING
  public String generateToken(UserDetailsImpl userDetails) {
    Map<String, Object> claims = new HashMap<>();
    String jwt = Jwts.builder().setClaims(claims)
        .setSubject(userDetails.getUsername())
        .claim("user_id", userDetails.getId())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  //10h
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
    return jwt;
  }
  //endregion

  //region EXTRACTING
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Integer extractUserId(String token) {
    Claims claims = extractAllClaims(token);
    return (Integer) claims.get("user_id");
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
  //endregion

  //region VALIDATING
  private boolean isTokenExpired(String token) {
    return extractExpiration(token)
        .before(new Date());
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
  //endregion
}