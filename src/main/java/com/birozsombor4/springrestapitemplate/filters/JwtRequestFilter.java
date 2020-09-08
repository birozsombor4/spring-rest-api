package com.birozsombor4.springrestapitemplate.filters;

import com.birozsombor4.springrestapitemplate.exceptions.InvalidAuthorizationHeaderException;
import com.birozsombor4.springrestapitemplate.exceptions.InvalidJwtFormatException;
import com.birozsombor4.springrestapitemplate.exceptions.InvalidJwtTokenException;
import com.birozsombor4.springrestapitemplate.exceptions.MissingUsernameException;
import com.birozsombor4.springrestapitemplate.utils.JwtUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private UserDetailsService userDetailsService;
  private JwtUtil jwtUtil;

  @Autowired
  public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException,
      InvalidJwtTokenException {
    String path = request.getRequestURI();
    if (path.equals("/register") || path.equals("/login") || path.equals("/verify")) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorizationHeader = request.getHeader("Authorization");
    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        jwt = authorizationHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);
      } catch (Exception e) {
        throw new InvalidJwtFormatException();
      }
    } else {
      throw new InvalidAuthorizationHeaderException();
    }

    UserDetails userDetails = null;
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      userDetails = getUserDetailsByUsername(username);
      if (jwtUtil.validateToken(jwt, userDetails)) {
        setAuthenticationForUser(userDetails, request);
      } else {
        throw new InvalidJwtTokenException();
      }
    } else {
      throw new MissingUsernameException();
    }
    filterChain.doFilter(request, response);
  }

  private void setAuthenticationForUser(UserDetails userDetails, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
  }

  private UserDetails getUserDetailsByUsername(String username) {
    UserDetails userDetails = null;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (UsernameNotFoundException e) {
      throw new UsernameNotFoundException("Username is not found: " + username);
    }
    return userDetails;
  }
}