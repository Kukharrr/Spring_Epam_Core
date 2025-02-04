package com.example.task_Spring_EPAM.service;

import com.example.task_Spring_EPAM.dao.TraineeDAO;
import com.example.task_Spring_EPAM.entity.Trainee;
import com.example.task_Spring_EPAM.util.UsernamePasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDAO traineeDAO;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public void createTrainee(Trainee trainee) {
        int serialNumber = 0;
        String baseUsername = UsernamePasswordGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String username = baseUsername;
        while (traineeDAO.findByUsername(username).isPresent()) {
            serialNumber++;
            username = baseUsername + serialNumber;
        }
        String password = UsernamePasswordGenerator.generatePassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        traineeDAO.save(trainee);

        logger.info("Created trainee with username: {}", trainee.getUsername());
    }

    public Optional<Trainee> findTraineeByUsername(String username) {
        logger.info("Finding trainee by username: {}", username);
        return traineeDAO.findByUsername(username);
    }

    public void deleteTrainee(String username) {
        logger.info("Deleting trainee by username: {}", username);
        traineeDAO.deleteByUsername(username);
    }

    public void updateTrainee(Trainee trainee) {
        logger.info("Updating trainee with username: {}", trainee.getUsername());

        Optional<Trainee> existingTraineeOpt = traineeDAO.findByUsername(trainee.getUsername());
        existingTraineeOpt.ifPresent(existingTrainee -> {
            Optional.ofNullable(trainee.getFirstName()).ifPresent(existingTrainee::setFirstName);
            Optional.ofNullable(trainee.getLastName()).ifPresent(existingTrainee::setLastName);
            Optional.ofNullable(trainee.getPassword()).ifPresent(existingTrainee::setPassword);
            Optional.ofNullable(trainee.getDateOfBirth()).ifPresent(existingTrainee::setDateOfBirth);
            Optional.ofNullable(trainee.getAddress()).ifPresent(existingTrainee::setAddress);
            existingTrainee.setActive(trainee.isActive());

            traineeDAO.updateTrainee(existingTrainee);
        });
    }

    public Collection<Trainee> getAllTrainees() {
        logger.info("Getting all trainees");
        return traineeDAO.getAllTrainees();
    }
}
