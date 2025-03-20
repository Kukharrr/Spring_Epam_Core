package com.example.epam.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class UsernamePasswordGenerator {

    private final BCryptPasswordEncoder passwordEncoder;

    public UsernamePasswordGenerator() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name cannot be null");
        }
        return (firstName.toLowerCase() + "." + lastName.toLowerCase()).replace(" ", "");
    }

    public String generatePassword() {
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 10);
    }

    public <T> String generateUniqueUsername(String firstName, String lastName, Function<String, Optional<T>> findByUsernameFunction) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name cannot be null");
        }
        int attempt = 0;
        String baseUsername = generateUsername(firstName, lastName);
        String username = baseUsername;

        while (findByUsernameFunction.apply(username).isPresent()) {
            attempt++;
            username = baseUsername + attempt;
        }
        return username;
    }

    public String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(rawPassword);
    }
}
