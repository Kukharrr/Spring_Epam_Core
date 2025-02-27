package com.example.epam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingTypeDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("trainingTypeName")
    private String trainingTypeName;

    public TrainingTypeDTO(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }

    public Long getId() {
        return id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }
}