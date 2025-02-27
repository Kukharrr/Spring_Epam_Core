package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivationStatus {
    @JsonProperty("active")
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }
}