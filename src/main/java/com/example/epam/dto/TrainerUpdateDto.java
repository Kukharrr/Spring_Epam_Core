package com.example.epam.dto;

import com.example.epam.entity.TrainingType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class TrainerUpdateDto {
    @JsonProperty("username")
    @NotBlank
    private String username;

    @JsonProperty("firstName")
    @NotBlank
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank
    private String lastName;

    @JsonProperty("specialization")
    private TrainingType specialization;

    @JsonProperty("active")
    @NotBlank
    private Boolean isActive;

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}