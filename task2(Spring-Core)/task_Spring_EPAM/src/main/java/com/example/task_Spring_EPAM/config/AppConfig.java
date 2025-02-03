package com.example.task_Spring_EPAM.config;

import com.example.task_Spring_EPAM.entity.Trainee;
import com.example.task_Spring_EPAM.entity.Trainer;
import com.example.task_Spring_EPAM.entity.Training;
import com.example.task_Spring_EPAM.service.TraineeService;
import com.example.task_Spring_EPAM.service.TrainerService;
import com.example.task_Spring_EPAM.service.TrainingService;
import com.example.task_Spring_EPAM.util.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.example.task_Spring_EPAM")
public class AppConfig {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Value("${trainee.data.file}")
    private String traineeDataFile;

    @Value("${trainer.data.file}")
    private String trainerDataFile;

    @Value("${training.data.file}")
    private String trainingDataFile;

    @Autowired
    public AppConfig(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @PostConstruct
    public void init() {
        List<Trainee> trainees = DataInitializer.loadTraineesFromFile(traineeDataFile);
        List<Trainer> trainers = DataInitializer.loadTrainersFromFile(trainerDataFile);
        List<Training> trainings = DataInitializer.loadTrainingsFromFile(trainingDataFile);

        trainees.forEach(traineeService::createTrainee);
        trainers.forEach(trainerService::createTrainer);
        trainings.forEach(trainingService::createTraining);
    }
}
