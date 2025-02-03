package com.example.task_Spring_EPAM.dao;

import com.example.task_Spring_EPAM.entity.Trainee;
import com.example.task_Spring_EPAM.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TraineeDAO {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDAO.class);

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void save(Trainee trainee) {
        storage.getTrainees().put(trainee.getUsername(), trainee);
        logger.info("Saved trainee with username: {}", trainee.getUsername());
    }

    public Optional<Trainee> findByUsername(String username) {
        logger.info("Looking for trainee with username: {}", username);
        return Optional.ofNullable(storage.getTrainees().get(username));
    }

    public void deleteByUsername(String username) {
        storage.getTrainees().remove(username);
        logger.info("Deleted trainee with username: {}", username);
    }

    public void updateTrainee(Trainee trainee) {
        storage.getTrainees().put(trainee.getUsername(), trainee);
        logger.info("Updated trainee with username: {}", trainee.getUsername());
    }

    public Collection<Trainee> getAllTrainees() {
        logger.info("Retrieved all trainees");
        return storage.getTrainees().values();
    }
}
