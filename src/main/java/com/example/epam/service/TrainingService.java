package com.example.epam.service;

import com.example.epam.dao.TrainingDao;

import com.example.epam.entity.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);
    private final TrainingDao trainingDAO;

    @Autowired
    public TrainingService(TrainingDao trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public void createTraining(Training training) {
        trainingDAO.save(training);
        logger.info("Created training with ID: {}", training.getId());
    }

    public Optional<Training> findTrainingById(Long id) {
        logger.info("Finding training by ID: {}", id);
        return trainingDAO.findById(id);
    }

    public void deleteTraining(Long id) {
        logger.info("Deleting training by ID: {}", id);
        trainingDAO.delete(id);
    }

    public void updateTraining(Training training) {
        logger.info("Updating training with ID: {}", training.getId());
        Optional<Training> existingTrainingOpt = trainingDAO.findById(training.getId());
        existingTrainingOpt.ifPresent(existingTraining -> {
            Optional.ofNullable(training.getTrainee()).ifPresent(existingTraining::setTrainee);
            Optional.ofNullable(training.getTrainer()).ifPresent(existingTraining::setTrainer);
            Optional.ofNullable(training.getTrainingName()).ifPresent(existingTraining::setTrainingName);
            Optional.ofNullable(training.getTrainingType()).ifPresent(existingTraining::setTrainingType);
            Optional.ofNullable(training.getTrainingDate()).ifPresent(existingTraining::setTrainingDate);
            existingTraining.setTrainingDuration(training.getTrainingDuration());
            trainingDAO.updateTraining(existingTraining);
        });
    }

    public Collection<Training> getAllTrainings() {
        logger.info("Getting all trainings");
        return trainingDAO.getAll();
    }

    public Collection<Training> getTrainingsByTrainee(String traineeUsername, Date startDate, Date endDate, String trainerName, String trainingType) {
        return trainingDAO.getAll().stream()
                .filter(training -> training.getTrainee().getUser().getUsername().equals(traineeUsername))
                .filter(training -> (startDate == null || !training.getTrainingDate().before(startDate)) &&
                        (endDate == null || !training.getTrainingDate().after(endDate)))
                .filter(training -> trainerName == null || training.getTrainer().getUser().getUsername().equals(trainerName))
                .filter(training -> trainingType == null || training.getTrainingType().equals(trainingType))
                .collect(Collectors.toList());
    }

    public Collection<Training> getTrainingsByTrainer(String trainerUsername, Date startDate, Date endDate, String traineeName) {
        return trainingDAO.getAll().stream()
                .filter(training -> training.getTrainer().getUser().getUsername().equals(trainerUsername))
                .filter(training -> (startDate == null || !training.getTrainingDate().before(startDate)) &&
                        (endDate == null || !training.getTrainingDate().after(endDate)))
                .filter(training -> traineeName == null || training.getTrainee().getUser().getUsername().equals(traineeName))
                .collect(Collectors.toList());
    }
}
