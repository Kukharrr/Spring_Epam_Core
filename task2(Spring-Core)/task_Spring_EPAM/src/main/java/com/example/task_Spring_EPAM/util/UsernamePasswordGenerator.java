package com.example.task_Spring_EPAM.util;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class UsernamePasswordGenerator {

    public static String generateUsername(String firstName, String lastName) {
        return (firstName.toLowerCase() + "." + lastName.toLowerCase()).replace(" ", "");
    }

    public static String generatePassword() {
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 10);
    }

    public <T> String generateUniqueUsername(String firstName, String lastName, Function<String, Optional<T>> findByUsernameFunction) {
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
