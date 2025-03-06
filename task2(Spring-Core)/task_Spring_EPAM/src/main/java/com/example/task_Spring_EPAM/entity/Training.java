package com.example.task_Spring_EPAM.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Training {
    private int id;
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private String trainingType;
    private LocalDate trainingDate;
    private int trainingDuration;
}
