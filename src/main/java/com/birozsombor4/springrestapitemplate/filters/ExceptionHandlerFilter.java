package com.birozsombor4.springrestapitemplate.filters;

import com.birozsombor4.springrestapitemplate.exceptions.InvalidAuthorizationHeaderException;
import com.birozsombor4.springrestapitemplate.exceptions.InvalidJwtFormatException;
import com.birozsombor4.springrestapitemplate.exceptions.InvalidJwtTokenException;
import com.birozsombor4.springrestapitemplate.exceptions.MissingUsernameException;
import com.birozsombor4.springrestapitemplate.models.dtos.ErrorDTO;
import com.birozsombor4.springrestapitemplate.utils.JsonUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (InvalidAuthorizationHeaderException e) {
      setResponseToUnauthorizedAndWriteMessage(response, "Invalid Authorization or missing JWT token.");
    } catch (InvalidJwtFormatException e) {
      setResponseToUnauthorizedAndWriteMessage(response, "Invalid JWT format.");
    } catch (UsernameNotFoundException e) {
      setResponseToUnauthorizedAndWriteMessage(response, "Username not found.");
    } catch (MissingUsernameException e) {
      setResponseToUnauthorizedAndWriteMessage(response, "Username is missing from JWT.");
    } catch (InvalidJwtTokenException e) {
      setResponseToUnauthorizedAndWriteMessage(response, "Expired JWT.");
    }
  }

  private void setResponseToUnauthorizedAndWriteMessage(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setHeader("Content-Type", MediaType.APPLICATION_JSON.toString());
    response.getWriter().write(JsonUtil.convertObjectToJson(new ErrorDTO("error", message)));
  }
}