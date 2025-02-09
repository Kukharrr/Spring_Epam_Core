package com.example.task_Spring_EPAM.storage;

import com.example.task_Spring_EPAM.entity.Trainee;
import com.example.task_Spring_EPAM.entity.Trainer;
import com.example.task_Spring_EPAM.entity.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Storage {

    private Map<String, Trainee> trainees = new HashMap<>();
    private Map<String, Trainer> trainers = new HashMap<>();
    private Map<Integer, Training> trainings = new HashMap<>();

    public Map<String, Trainee> getTrainees() {
        return trainees;
    }

    public Map<String, Trainer> getTrainers() {
        return trainers;
    }

    public Map<Integer, Training> getTrainings() {
        return trainings;
    }
}
