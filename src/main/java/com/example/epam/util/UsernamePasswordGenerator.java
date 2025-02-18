package com.example.epam.util;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class UsernamePasswordGenerator {

    public static String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name cannot be null");
        }
        return (firstName.toLowerCase() + "." + lastName.toLowerCase()).replace(" ", "");
    }

    public static String generatePassword() {
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 10);
    }

    public <T> String generateUniqueUsername(String firstName, String lastName, Function<String, Optional<T>> findByUsernameFunction) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name cannot be null");
        }
        int serialNumber = 0;
        String baseUsername = generateUsername(firstName, lastName);
        String username = baseUsername;
        while (findByUsernameFunction.apply(username).isPresent()) {
            serialNumber++;
            username = baseUsername + serialNumber;
        }
        return username;
    }
}
