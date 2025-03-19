package com.example.epam.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if (path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/favicon.ico") ||
                (path.startsWith("/trainees") && httpRequest.getMethod().equals("POST")) ||
                (path.startsWith("/trainers") && httpRequest.getMethod().equals("POST")) ||
                path.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            logger.error("Authentication required, transactionId: {}, path: {}", transactionId, path);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }

        logger.info("Authenticated session, transactionId: {}, username: {}, path: {}", transactionId, session.getAttribute("username"), path);
        chain.doFilter(request, response);
    }
}