package com.example.task_Spring_EPAM.controller;

import com.example.task_Spring_EPAM.entity.Trainer;
import com.example.task_Spring_EPAM.exception.ResourceNotFoundException;
import com.example.task_Spring_EPAM.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public Trainer createTrainer(@RequestBody Trainer trainer) {
        logger.info("Creating trainer with first name: {} and last name: {}", trainer.getFirstName(), trainer.getLastName());
        trainerService.createTrainer(trainer);
        return trainer;
    }

    @GetMapping
    public Collection<Trainer> getAllTrainers() {
        logger.info("Getting all trainers");
        return trainerService.getAllTrainers();
    }

    @GetMapping("/{username}")
    public Trainer getTrainerByUsername(@PathVariable String username) {
        logger.info("Getting trainer with username: {}", username);
        return trainerService.findTrainerByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));
    }

    @PutMapping
    public Trainer updateTrainer(@RequestBody Trainer trainer) {
        logger.info("Updating trainer with username: {}", trainer.getUsername());
        if (!trainerService.findTrainerByUsername(trainer.getUsername()).isPresent()) {
            throw new ResourceNotFoundException("Trainer not found with username: " + trainer.getUsername());
        }
        trainerService.updateTrainer(trainer);
        return trainer;
    }

    @DeleteMapping("/{username}")
    public void deleteTrainer(@PathVariable String username) {
        logger.info("Deleting trainer with username: {}", username);
        if (!trainerService.findTrainerByUsername(username).isPresent()) {
            throw new ResourceNotFoundException("Trainer not found with username: " + username);
        }
        trainerService.deleteTrainer(username);
    }
}
