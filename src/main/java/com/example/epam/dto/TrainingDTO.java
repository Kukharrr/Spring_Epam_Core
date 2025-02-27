package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class TrainingDTO {
    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("trainingDate")
    private LocalDate trainingDate;

    @JsonProperty("trainingType")
    private String trainingType;

    @JsonProperty("trainingDuration")
    private Integer trainingDuration;

    @JsonProperty("trainerName")
    private String trainerName;

    @JsonProperty("traineeName")
    private String traineeName;

    public TrainingDTO(String trainingName, LocalDate trainingDate, String trainingType, Integer trainingDuration, String trainerName, String traineeName) {
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingType = trainingType;
        this.trainingDuration = trainingDuration;
        this.trainerName = trainerName;
        this.traineeName = traineeName;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public Integer getTrainingDuration() {
        return trainingDuration;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getTraineeName() {
        return traineeName;
    }
}