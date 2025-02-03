package com.example.task_Spring_EPAM.dao;

import com.example.task_Spring_EPAM.entity.Training;
import com.example.task_Spring_EPAM.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainingDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void save(Training training) {
        storage.getTrainings().put(training.getId(), training);
        logger.info("Saved training with ID: {}", training.getId());
    }

    public Optional<Training> findById(int id) {
        logger.info("Looking for training with ID: {}", id);
        return Optional.ofNullable(storage.getTrainings().get(id));
    }

    public void deleteById(int id) {
        storage.getTrainings().remove(id);
        logger.info("Deleted training with ID: {}", id);
    }

    public void updateTraining(Training training) {
        storage.getTrainings().put(training.getId(), training);
        logger.info("Updated training with ID: {}", training.getId());
    }

    public Collection<Training> getAllTrainings() {
        logger.info("Retrieved all trainings");
        return storage.getTrainings().values();
    }
}
