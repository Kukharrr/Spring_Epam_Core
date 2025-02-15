package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class TrainerCreateDto {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("specialization")
    private String specialization;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }
}
