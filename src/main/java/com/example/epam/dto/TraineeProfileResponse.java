package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class TraineeProfileResponse {
    @JsonProperty("username")
    private String username;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @JsonProperty("address")
    private String address;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("trainers")
    private List<String> trainers;

    public TraineeProfileResponse(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, Boolean isActive, List<String> trainers) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
        this.trainers = trainers;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public List<String> getTrainers() {
        return trainers;
    }
}