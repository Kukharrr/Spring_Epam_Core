package com.example.task_Spring_EPAM.controller;

import com.example.task_Spring_EPAM.entity.Trainee;
import com.example.task_Spring_EPAM.exception.ResourceNotFoundException;
import com.example.task_Spring_EPAM.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    public Trainee createTrainee(@RequestBody Trainee trainee) {
        logger.info("Creating trainee with first name: {} and last name: {}", trainee.getFirstName(), trainee.getLastName());
        traineeService.createTrainee(trainee);
        return trainee;
    }

    @GetMapping
    public Collection<Trainee> getAllTrainees() {
        logger.info("Getting all trainees");
        return traineeService.getAllTrainees();
    }

    @GetMapping("/{username}")
    public Trainee getTraineeByUsername(@PathVariable String username) {
        logger.info("Getting trainee with username: {}", username);
        return traineeService.findTraineeByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));
    }

    @PutMapping
    public Trainee updateTrainee(@RequestBody Trainee trainee) {
        logger.info("Updating trainee with username: {}", trainee.getUsername());
        if (!traineeService.findTraineeByUsername(trainee.getUsername()).isPresent()) {
            throw new ResourceNotFoundException("Trainee not found with username: " + trainee.getUsername());
        }
        traineeService.updateTrainee(trainee);
        return trainee;
    }

    @DeleteMapping("/{username}")
    public void deleteTrainee(@PathVariable String username) {
        logger.info("Deleting trainee with username: {}", username);
        if (!traineeService.findTraineeByUsername(username).isPresent()) {
            throw new ResourceNotFoundException("Trainee not found with username: " + username);
        }
        traineeService.deleteTrainee(username);
    }
}
