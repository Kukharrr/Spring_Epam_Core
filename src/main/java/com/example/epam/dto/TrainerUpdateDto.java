package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class TrainerUpdateDto {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("specialization")
    private String specialization;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("active")
    private Boolean isActive;


    public String getSpecialization() {
        return specialization;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername(){ return username; }

    public String getPassword(){ return password; }

    public Boolean getIsActive() {return isActive;}
}
