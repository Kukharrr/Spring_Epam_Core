package com.example.epam.controller;

import com.example.epam.dto.*;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.Training;
import com.example.epam.entity.User;
import com.example.epam.service.AuthService;
import com.example.epam.service.TraineeService;
import com.example.epam.service.TrainingService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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

@OpenAPIDefinition(info = @Info(title = "Trainee API", version = "1.0", description = "API for managing trainees"))
@RestController
@RequestMapping("/trainees")
public class TraineeController {
    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    @Autowired
    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @PostMapping
    @Operation(summary = "Register a new trainee", description = "Creates a new trainee profile with generated username and password")
    @ApiResponse(responseCode = "201", description = "Trainee registered successfully")
    public ResponseEntity<RegistrationResponse> createTrainee(@Valid @RequestBody TraineeCreateDto traineeCreateDto) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Registering new trainee, transactionId: {}, firstName: {}, lastName: {}", transactionId, traineeCreateDto.getFirstName(), traineeCreateDto.getLastName());
        Trainee trainee = traineeService.createTrainee(traineeCreateDto);
        RegistrationResponse response = new RegistrationResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee profile", description = "Retrieve a trainee's profile after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting trainee profile, transactionId: {}, username: {}", transactionId, username);
        Trainee trainee = traineeService.getTraineeProfileWithTrainers(username);
        TraineeProfileResponse response = new TraineeProfileResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getIsActive(),
                trainee.getTrainers().stream().map(Trainer::getUser).map(User::getUsername).collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/password")
    @Operation(summary = "Update trainee password", description = "Change trainee password after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @Parameter(name = "password", description = "New password", required = true)
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> updatePassword(@PathVariable String username, @RequestBody PasswordUpdate passwordUpdate) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainee password, transactionId: {}, username: {}", transactionId, username);
        traineeService.updatePassword(username, passwordUpdate.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update trainee profile", description = "Update a trainee's profile after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<TraineeProfileResponse> updateTrainee(@RequestParam String username, @Valid @RequestBody TraineeUpdateDto traineeUpdateDto) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainee, transactionId: {}, username: {}", transactionId, username);
        Trainee updatedTrainee = traineeService.updateTrainee(traineeUpdateDto);
        TraineeProfileResponse response = new TraineeProfileResponse(
                updatedTrainee.getUser().getUsername(),
                updatedTrainee.getUser().getFirstName(),
                updatedTrainee.getUser().getLastName(),
                updatedTrainee.getDateOfBirth(),
                updatedTrainee.getAddress(),
                updatedTrainee.getUser().getIsActive(),
                updatedTrainee.getTrainers().stream().map(Trainer::getUser).map(User::getUsername).collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/activate")
    @Operation(summary = "Activate/deactivate trainee", description = "Toggle trainee activation status after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> activateTrainee(@PathVariable String username, @RequestBody ActivationStatus status) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Activating trainee, transactionId: {}, username: {}, isActive: {}", transactionId, username, status.isActive());
        traineeService.setActiveStatus(username, status.isActive());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Delete a trainee profile after login (hard delete with cascade)")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Profile deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Deleting trainee, transactionId: {}, username: {}", transactionId, username);
        traineeService.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/not-assigned-trainers")
    @Operation(summary = "Get not assigned active trainers for trainee", description = "Retrieve active trainers not assigned to the trainee after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<TrainerProfileResponse>> getNotAssignedTrainers(@PathVariable String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting not assigned trainers, transactionId: {}, username: {}", transactionId, username);
        List<Trainer> notAssignedTrainers = traineeService.getNotAssignedTrainers(username);
        List<TrainerProfileResponse> response = notAssignedTrainers.stream().map(trainer -> new TrainerProfileResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                trainer.getUser().getIsActive(),
                null
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update trainee's trainer list", description = "Update the list of trainers for a trainee after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Trainers list updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<String>> updateTrainersList(@PathVariable String username, @RequestBody List<String> trainerUsernames) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainee trainers list, transactionId: {}, username: {}, trainerUsernames: {}", transactionId, username, trainerUsernames);
        traineeService.updateTrainersList(username, trainerUsernames);
        return ResponseEntity.ok(trainerUsernames);
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee trainings list", description = "Retrieve a trainee's trainings with optional filters after login")
    @Parameter(name = "username", description = "Trainee username for path", required = true)
    @Parameter(name = "from", description = "Start date for training period", required = false)
    @Parameter(name = "to", description = "End date for training period", required = false)
    @Parameter(name = "trainerName", description = "Trainer username filter", required = false)
    @Parameter(name = "trainingType", description = "Training type filter", required = false)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<TrainingDTO>> getTraineeTrainings(@PathVariable String username, @RequestParam LocalDate from, @RequestParam LocalDate to, @RequestParam String trainerName, @RequestParam String trainingType) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting trainee trainings, transactionId: {}, username: {}, from: {}, to: {}, trainerName: {}, trainingType: {}", transactionId, username, from, to, trainerName, trainingType);
        Collection<Training> trainings = trainingService.getTrainingsByTraineeWithRelations(username, from, to, trainerName, trainingType);
        List<TrainingDTO> response = trainings.stream().map(training -> new TrainingDTO(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType().getTrainingTypeName(),
                training.getTrainingDuration(),
                training.getTrainer().getUser().getUsername(),
                training.getTrainee().getUser().getUsername()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}