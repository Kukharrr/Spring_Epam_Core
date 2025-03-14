package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentials {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}