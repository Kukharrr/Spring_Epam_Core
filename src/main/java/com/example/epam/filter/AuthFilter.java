package com.example.epam.filter;

import com.example.epam.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // Allow public endpoints to pass through
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

        // Check for Authorization header with Bearer token
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authentication required, transactionId: {}, path: {}", transactionId, path);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header with Bearer token required");
            return;
        }

        // Extract and validate JWT token
        String token = authHeader.substring(7);
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                logger.info("Authenticated request, transactionId: {}, username: {}, path: {}", transactionId, username, path);
                chain.doFilter(request, response);
            } else {
                logger.error("Invalid JWT token, transactionId: {}, path: {}", transactionId, path);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
            }
        } catch (Exception e) {
            logger.error("JWT validation failed, transactionId: {}, path: {}, error: {}", transactionId, path, e.getMessage());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        }
    }
}