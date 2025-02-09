package com.example.task_Spring_EPAM.service;

import com.example.task_Spring_EPAM.dao.TrainerDAO;
import com.example.task_Spring_EPAM.entity.Trainer;
import com.example.task_Spring_EPAM.util.UsernamePasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDAO trainerDAO;
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setUsernamePasswordGenerator(UsernamePasswordGenerator usernamePasswordGenerator) {
        this.usernamePasswordGenerator = usernamePasswordGenerator;
    }

    public void createTrainer(Trainer trainer) {
        String username = usernamePasswordGenerator.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName(), trainerDAO::findByUsername);
        String password = UsernamePasswordGenerator.generatePassword();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        trainerDAO.save(trainer);

        logger.info("Created trainer with username: {}", trainer.getUsername());
    }

    public Optional<Trainer> findTrainerByUsername(String username) {
        logger.info("Finding trainer by username: {}", username);
        return trainerDAO.findByUsername(username);
    }

    public void deleteTrainer(String username) {
        logger.info("Deleting trainer by username: {}", username);
        trainerDAO.deleteByUsername(username);
    }

    public void updateTrainer(Trainer trainer) {
        logger.info("Updating trainer with username: {}", trainer.getUsername());

        Optional<Trainer> existingTrainerOpt = trainerDAO.findByUsername(trainer.getUsername());
        existingTrainerOpt.ifPresent(existingTrainer -> {
            Optional.ofNullable(trainer.getFirstName()).ifPresent(existingTrainer::setFirstName);
            Optional.ofNullable(trainer.getLastName()).ifPresent(existingTrainer::setLastName);
            Optional.ofNullable(trainer.getPassword()).ifPresent(existingTrainer::setPassword);
            Optional.ofNullable(trainer.getSpecialization()).ifPresent(existingTrainer::setSpecialization);
            existingTrainer.setActive(trainer.isActive());

            trainerDAO.updateTrainer(existingTrainer);
        });
    }

    public Collection<Trainer> getAllTrainers() {
        logger.info("Getting all trainers");
        return trainerDAO.getAllTrainers();
    }
}
