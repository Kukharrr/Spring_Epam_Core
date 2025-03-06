package com.example.epam.dto;

import com.example.epam.entity.TrainingType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class TrainerCreateDto {
    @JsonProperty("firstName")
    @NotBlank
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank
    private String lastName;

    @JsonProperty("specialization")
    @NotBlank
    private TrainingType specialization;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }
}