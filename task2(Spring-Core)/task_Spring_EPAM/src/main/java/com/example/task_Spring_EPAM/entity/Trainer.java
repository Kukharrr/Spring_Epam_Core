package com.example.task_Spring_EPAM.entity;

import lombok.Data;

@Data
public class Trainer {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;
    private String specialization;
}
