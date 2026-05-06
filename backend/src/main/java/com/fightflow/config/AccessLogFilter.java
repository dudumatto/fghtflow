package com.fightflow.config;

import com.fightflow.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AccessLogFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long start = System.currentTimeMillis();
    try {
      filterChain.doFilter(request, response);
    } finally {
      long tookMs = System.currentTimeMillis() - start;

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      Long userId = null;
      String role = null;
      if (auth != null && auth.getPrincipal() instanceof UserPrincipal up) {
        userId = up.getId();
        role = up.getRole().name();
      }

      log.info("access method={} path={} status={} tookMs={} userId={} role={}",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          tookMs,
          userId,
          role);
    }
  }
}

