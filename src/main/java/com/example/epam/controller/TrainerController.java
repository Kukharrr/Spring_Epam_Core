package com.example.epam.controller;

import com.example.epam.dto.*;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.Training;
import com.example.epam.entity.User;
import com.example.epam.service.AuthService;
import com.example.epam.service.TrainerService;
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

@OpenAPIDefinition(info = @Info(title = "Trainer API", version = "1.0", description = "API for managing trainers"))
@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public TrainerController(TrainerService trainerService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @PostMapping
    @Operation(summary = "Register a new trainer", description = "Creates a new trainer profile with generated username and password")
    @ApiResponse(responseCode = "201", description = "Trainer registered successfully")
    public ResponseEntity<RegistrationResponse> createTrainer(@Valid @RequestBody TrainerCreateDto trainerCreateDto) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Registering new trainer, transactionId: {}, firstName: {}, lastName: {}", transactionId, trainerCreateDto.getFirstName(), trainerCreateDto.getLastName());

        Trainer trainer = trainerService.createTrainer(trainerCreateDto);

        RegistrationResponse response = new RegistrationResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getRawPassword()
        );

        return ResponseEntity.status(201).body(response);
    }


    @GetMapping
    @Operation(summary = "Get all trainers", description = "Retrieve a list of all trainers after login")
    @Parameter(name = "username", description = "Trainer username for path", required = false)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<Trainer>> getTrainers() {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting all trainers, transactionId: {}", transactionId);
        List<Trainer> trainers = trainerService.getAllTrainersWithTrainees();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile", description = "Retrieve a trainer's profile after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting trainer profile, transactionId: {}, username: {}", transactionId, username);
        Trainer trainer = trainerService.getTrainerProfileWithTrainees(username);
        TrainerProfileResponse response = new TrainerProfileResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                trainer.getUser().getIsActive(),
                trainer.getTrainees().stream().map(Trainee::getUser).map(User::getUsername).collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/password")
    @Operation(summary = "Update trainer password", description = "Change trainer password after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @Parameter(name = "password", description = "New password", required = true)
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> updatePassword(@PathVariable String username, @RequestBody PasswordUpdate passwordUpdate) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainer password, transactionId: {}, username: {}", transactionId, username);
        trainerService.updatePassword(username, passwordUpdate.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update trainer profile", description = "Update a trainer's profile after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<TrainerProfileResponse> updateTrainer(@RequestParam String username, @Valid @RequestBody TrainerUpdateDto trainer) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainer, transactionId: {}, username: {}", transactionId, username);
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);
        TrainerProfileResponse response = new TrainerProfileResponse(
                updatedTrainer.getUser().getUsername(),
                updatedTrainer.getUser().getFirstName(),
                updatedTrainer.getUser().getLastName(),
                updatedTrainer.getSpecialization().getTrainingTypeName(),
                updatedTrainer.getUser().getIsActive(),
                updatedTrainer.getTrainees().stream().map(Trainee::getUser).map(User::getUsername).collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/activate")
    @Operation(summary = "Activate/deactivate trainer", description = "Toggle trainer activation status after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> activateTrainer(@PathVariable String username, @RequestBody ActivationStatus status) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Activating trainer, transactionId: {}, username: {}, isActive: {}", transactionId, username, status.isActive());
        trainerService.setActiveStatus(username, status.isActive());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainer profile", description = "Delete a trainer profile after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Profile deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<Void> deleteTrainer(@PathVariable String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Deleting trainer, transactionId: {}, username: {}", transactionId, username);
        trainerService.deleteTrainer(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/not-assigned-trainees")
    @Operation(summary = "Get not assigned active trainees for trainer", description = "Retrieve active trainees not assigned to the trainer after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<TraineeProfileResponse>> getNotAssignedTrainees(@PathVariable String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting not assigned trainees, transactionId: {}, username: {}", transactionId, username);
        List<Trainee> notAssignedTrainees = trainerService.getNotAssignedTrainees(username);
        List<TraineeProfileResponse> response = notAssignedTrainees.stream().map(trainee -> new TraineeProfileResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getIsActive(),
                null
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer trainings list", description = "Retrieve a trainer's trainings with optional filters after login")
    @Parameter(name = "username", description = "Trainer username for path", required = true)
    @Parameter(name = "from", description = "Start date for training period")
    @Parameter(name = "to", description = "End date for training period")
    @Parameter(name = "traineeName", description = "Trainee username filter")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Login required")
    public ResponseEntity<List<TrainingDTO>> getTrainerTrainings(@PathVariable String username, @RequestParam LocalDate from, @RequestParam LocalDate to, @RequestParam String traineeName) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting trainer trainings, transactionId: {}, username: {}, from: {}, to: {}, traineeName: {}", transactionId, username, from, to, traineeName);
        Collection<Training> trainings = trainingService.getTrainingsByTrainer(username, from, to, traineeName);
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