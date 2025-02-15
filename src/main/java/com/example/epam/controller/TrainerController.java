package com.example.epam.controller;

import com.example.epam.dto.*;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import com.example.epam.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<Trainer> createTrainer(@RequestBody TrainerCreateDto trainerCreateDto) {
        Trainer trainer = trainerService.createTrainer(trainerCreateDto);
        return ResponseEntity.ok(trainer);
    }

    @GetMapping
    public ResponseEntity<List<Trainer>> getTrainees() {
        List<Trainer> trainees = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainees);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Trainer> getTrainerByUsername(@PathVariable String username) {
        Optional<Trainer> trainer = trainerService.findTrainerByUsername(username);
        return trainer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/match")
    public ResponseEntity<Boolean> matchTrainerCredentials(@RequestBody UserCredentials credentials) {
        boolean match = trainerService.matchTrainerCredentials(credentials.getUsername(), credentials.getPassword());
        return ResponseEntity.ok(match);
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String username, @RequestBody PasswordUpdate passwordUpdate) {
        trainerService.updatePassword(username, passwordUpdate.getPassword());
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Trainer> updateTrainer( @RequestBody TrainerUpdateDto trainer) {
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);
        return ResponseEntity.ok(updatedTrainer);
    }

    @PutMapping("/{username}/activate")
    public ResponseEntity<Void> activateTrainer(@PathVariable String username, @RequestBody ActivationStatus status) {
        trainerService.setActiveStatus(username, status.isActive());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable String username) {
        trainerService.deleteTrainer(username);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/unassigned/{traineeUsername}")
//    public ResponseEntity<Collection<Trainer>> getUnassignedTrainers(@PathVariable String traineeUsername) {
//        Collection<Trainer> unassignedTrainers = trainerService.getUnassignedTrainers(traineeUsername);
//        return ResponseEntity.ok(unassignedTrainers);
//    }
}
