package com.example.task_Spring_EPAM.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Trainee {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;
    private LocalDate dateOfBirth;
    private String address;
}
