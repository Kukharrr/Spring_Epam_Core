package com.example.epam.controller;

import com.example.epam.entity.Training;
import com.example.epam.exception.ResourceNotFoundException;
import com.example.epam.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Training> createTraining(@RequestBody Training training) {
        if (training == null) {
            logger.error("Training is null");
            return ResponseEntity.badRequest().build();
        }
        trainingService.createTraining(training);
        return ResponseEntity.status(201).body(training); // HTTP status CREATED
    }

    @GetMapping
    public ResponseEntity<Collection<Training>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Training> getTrainingById(@PathVariable Long id) {
        return trainingService.findTrainingById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Training not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping
    public ResponseEntity<Training> updateTraining(@RequestBody Training training) {
        if (training == null) {
            logger.error("Training is null");
            return ResponseEntity.badRequest().build();
        }
        if (!trainingService.findTrainingById(training.getId()).isPresent()) {
            throw new ResourceNotFoundException("Training not found with ID: " + training.getId());
        }
        trainingService.updateTraining(training);
        return ResponseEntity.ok(training);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        if (!trainingService.findTrainingById(id).isPresent()) {
            throw new ResourceNotFoundException("Training not found with ID: " + id);
        }
        trainingService.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }
}
