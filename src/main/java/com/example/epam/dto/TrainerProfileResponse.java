package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TrainerProfileResponse {
    @JsonProperty("username")
    private String username;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("specialization")
    private String specialization;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("trainees")
    private List<String> trainees;

    public TrainerProfileResponse(String username, String firstName, String lastName, String specialization, Boolean isActive, List<String> trainees) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.isActive = isActive;
        this.trainees = trainees;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public List<String> getTrainees() {
        return trainees;
    }
}