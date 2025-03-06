package com.example.epam.controller;

import com.example.epam.service.AuthService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@OpenAPIDefinition(info = @Info(title = "Auth API", version = "1.0", description = "API for authentication"))
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    @Operation(summary = "Login", description = "Authenticate a user (trainee or trainer) and start a session")
    @Parameter(name = "username", description = "Username for authentication", required = true)
    @Parameter(name = "password", description = "Password for authentication", required = true)
    @ApiResponse(responseCode = "200", description = "Successful login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Login attempt, transactionId: {}, username: {}", transactionId, username);
        if (authService.authenticateTrainee(username, password) || authService.authenticateTrainer(username, password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("username", username);
            logger.info("Login successful, transactionId: {}, username: {}, response: 200", transactionId, username);
            return ResponseEntity.ok().build();
        }
        logger.error("Login failed, transactionId: {}, username: {}", transactionId, username);
        return ResponseEntity.status(401).build();
    }
}