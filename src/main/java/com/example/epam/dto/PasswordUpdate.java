package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordUpdate {
    @JsonProperty("password")
    private String password;

    public String getPassword() {
        return password;
    }
}