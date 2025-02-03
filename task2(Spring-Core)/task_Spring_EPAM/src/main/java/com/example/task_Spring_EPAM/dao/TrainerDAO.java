package com.example.task_Spring_EPAM.dao;

import com.example.task_Spring_EPAM.entity.Trainer;
import com.example.task_Spring_EPAM.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainerDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void save(Trainer trainer) {
        storage.getTrainers().put(trainer.getUsername(), trainer);
        logger.info("Saved trainer with username: {}", trainer.getUsername());
    }

    public Optional<Trainer> findByUsername(String username) {
        logger.info("Looking for trainer with username: {}", username);
        return Optional.ofNullable(storage.getTrainers().get(username));
    }

    public void deleteByUsername(String username) {
        storage.getTrainers().remove(username);
        logger.info("Deleted trainer with username: {}", username);
    }

    public void updateTrainer(Trainer trainer) {
        storage.getTrainers().put(trainer.getUsername(), trainer);
        logger.info("Updated trainer with username: {}", trainer.getUsername());
    }

    public Collection<Trainer> getAllTrainers() {
        logger.info("Retrieved all trainers");
        return storage.getTrainers().values();
    }
}
