package com.example.epam.controller;

import com.example.epam.service.AuthService;
import com.example.epam.service.BruteForceProtectionService;
import com.example.epam.service.TokenBlacklistService;
import com.example.epam.util.JwtUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@OpenAPIDefinition(info = @Info(title = "Auth API", version = "1.0", description = "API for authentication"))
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final BruteForceProtectionService bruteForceProtectionService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, BruteForceProtectionService bruteForceProtectionService,
                          TokenBlacklistService tokenBlacklistService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.bruteForceProtectionService = bruteForceProtectionService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate a user and return a JWT token")
    @Parameter(name = "username", description = "Username for authentication", required = true)
    @Parameter(name = "password", description = "Password for authentication", required = true)
    @ApiResponse(responseCode = "200", description = "Successful login, returns JWT token")
    @ApiResponse(responseCode = "401", description = "Invalid username or password")
    @ApiResponse(responseCode = "429", description = "User is blocked due to multiple failed login attempts")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Login attempt, transactionId: {}, username: {}", transactionId, username);

        if (bruteForceProtectionService.isBlocked(username)) {
            logger.error("User blocked, transactionId: {}, username: {}", transactionId, username);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Account is locked for 5 minutes due to too many failed login attempts.");
        }

        try {
            if (authService.authenticate(username, password)) {
                String token = jwtUtil.generateToken(username);
                logger.info("Login successful, transactionId: {}, username: {}", transactionId, username);
                return ResponseEntity.ok(token);
            } else {
                logger.warn("Invalid credentials, transactionId: {}, username: {}", transactionId, username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (RuntimeException ex) {
            logger.error("Authentication failed, transactionId: {}, username: {}, error: {}", transactionId, username, ex.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate the user's JWT token")
    @Parameter(name = "Authorization", description = "Bearer token in the Authorization header", required = true)
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or missing Authorization header")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Logout attempt, transactionId: {}", transactionId);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Invalid Authorization header, transactionId: {}", transactionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Authorization header with Bearer token required");
        }

        String token = authHeader.substring(7);
        tokenBlacklistService.blacklistToken(token);
        logger.info("Logout successful, transactionId: {}", transactionId);
        return ResponseEntity.ok("Logged out successfully");
    }
}