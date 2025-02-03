package com.example.task_Spring_EPAM.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Trainee {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;
    private Date dateOfBirth;
    private String address;
}
