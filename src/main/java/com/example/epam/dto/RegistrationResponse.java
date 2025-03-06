package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationResponse {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public RegistrationResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}