package com.example.task_Spring_EPAM.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Training {
    private int id;
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private String trainingType;
    private Date trainingDate;
    private int trainingDuration;
}
