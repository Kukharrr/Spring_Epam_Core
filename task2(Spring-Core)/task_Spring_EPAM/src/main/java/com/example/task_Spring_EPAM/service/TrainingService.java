package com.example.task_Spring_EPAM.service;

import com.example.task_Spring_EPAM.dao.TrainingDAO;
import com.example.task_Spring_EPAM.entity.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public void createTraining(Training training) {
        int id = generateTrainingId();
        training.setId(id);
        trainingDAO.save(training);
        logger.info("Created training with ID: {}", training.getId());
    }

    private int generateTrainingId() {
        return trainingDAO.getAllTrainings().size() + 1;
    }

    public Optional<Training> findTrainingById(int id) {
        logger.info("Finding training by ID: {}", id);
        return trainingDAO.findById(id);
    }

    public void deleteTraining(int id) {
        logger.info("Deleting training by ID: {}", id);
        trainingDAO.deleteById(id);
    }

    public void updateTraining(Training training) {
        logger.info("Updating training with ID: {}", training.getId());

        Optional<Training> existingTrainingOpt = trainingDAO.findById(training.getId());
        existingTrainingOpt.ifPresent(existingTraining -> {
            Optional.ofNullable(training.getTraineeUsername()).ifPresent(existingTraining::setTraineeUsername);
            Optional.ofNullable(training.getTrainerUsername()).ifPresent(existingTraining::setTrainerUsername);
            Optional.ofNullable(training.getTrainingName()).ifPresent(existingTraining::setTrainingName);
            Optional.ofNullable(training.getTrainingType()).ifPresent(existingTraining::setTrainingType);
            Optional.ofNullable(training.getTrainingDate()).ifPresent(existingTraining::setTrainingDate);
            existingTraining.setTrainingDuration(training.getTrainingDuration());

            trainingDAO.updateTraining(existingTraining);
        });
    }

    public Collection<Training> getAllTrainings() {
        logger.info("Getting all trainings");
        return trainingDAO.getAllTrainings();
    }
}
