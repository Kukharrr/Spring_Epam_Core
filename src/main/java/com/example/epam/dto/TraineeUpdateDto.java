package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public class TraineeUpdateDto {
    @JsonProperty("username")
    @NotBlank
    private String username;

    @JsonProperty("firstName")
    @NotBlank
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank
    private String lastName;

    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonProperty("address")
    private String address;

    @JsonProperty("password")
    private String password;

    @JsonProperty("active")
    @NotBlank
    private Boolean isActive;

    @JsonProperty("trainers")
    private List<String> trainers;

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

    public String getPassword() {
        return password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public List<String> getTrainers() {
        return trainers;
    }
}