package com.fightflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.exception.ErrorEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestSecurityHandlers {
  private static final Logger log = LoggerFactory.getLogger(RestSecurityHandlers.class);
  private final ObjectMapper om;

  public RestSecurityHandlers(ObjectMapper om) {
    this.om = om;
  }

  public AuthenticationEntryPoint entryPoint() {
    return (req, res, ex) -> write(res, req, 403, "Forbidden");
  }

  public AccessDeniedHandler accessDeniedHandler() {
    return (req, res, ex) -> {
      log.warn("security.denied method={} path={}", req.getMethod(), req.getRequestURI());
      write(res, req, 403, "Forbidden");
    };
  }

  private void write(HttpServletResponse res, HttpServletRequest req, int status, String message) throws IOException {
    res.setStatus(status);
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ErrorEnvelope err = new ErrorEnvelope(Instant.now(), status, message, req.getRequestURI());
    om.writeValue(res.getOutputStream(), ApiResponse.error(err));
  }
}
