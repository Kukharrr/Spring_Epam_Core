package com.example.epam.controller;


import com.example.epam.entity.Training;
import com.example.epam.exception.ResourceNotFoundException;
import com.example.epam.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public Training createTraining(@RequestBody Training training) {
        logger.info("Creating training with name: {}", training.getTrainingName());
        trainingService.createTraining(training);
        return training;
    }

    @GetMapping
    public Collection<Training> getAllTrainings() {
        logger.info("Getting all trainings");
        return trainingService.getAllTrainings();
    }

    @GetMapping("/{id}")
    public Training getTrainingById(@PathVariable Long id) {
        logger.info("Getting training with ID: {}", id);
        return trainingService.findTrainingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with ID: " + id));
    }

    @PutMapping
    public Training updateTraining(@RequestBody Training training) {
        logger.info("Updating training with ID: {}", training.getId());
        if (!trainingService.findTrainingById(training.getId()).isPresent()) {
            throw new ResourceNotFoundException("Training not found with ID: " + training.getId());
        }
        trainingService.updateTraining(training);
        return training;
    }

    @DeleteMapping("/{id}")
    public void deleteTraining(@PathVariable Long id) {
        logger.info("Deleting training with ID: {}", id);
        if (!trainingService.findTrainingById(id).isPresent()) {
            throw new ResourceNotFoundException("Training not found with ID: " + id);
        }
        trainingService.deleteTraining(id);
    }
}
