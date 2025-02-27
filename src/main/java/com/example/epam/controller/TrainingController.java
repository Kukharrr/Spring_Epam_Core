package com.example.epam.controller;

import com.example.epam.dto.TrainingDTO;
import com.example.epam.dto.TrainingTypeDTO;
import com.example.epam.entity.Training;
import com.example.epam.entity.TrainingType;
import com.example.epam.service.AuthService;
import com.example.epam.service.TrainingService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@OpenAPIDefinition(info = @Info(title = "Training API", version = "1.0", description = "API for managing trainings"))
@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    @Operation(summary = "Add a new training", description = "Create a training session after trainer login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Training created successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> createTraining(@RequestParam String username, @RequestBody Training training) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Creating training, transactionId: {}, username: {}, training: {}", transactionId, username, training);
        trainingService.createTraining(training, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/training-types")
    @Operation(summary = "Get all training types", description = "Retrieve all training types after trainer login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes(@RequestParam String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting training types, transactionId: {}, username: {}", transactionId, username);
        List<TrainingType> types = trainingService.getAllTrainingTypes();
        List<TrainingTypeDTO> response = types.stream().map(type -> new TrainingTypeDTO(type.getId(), type.getTrainingTypeName())).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}