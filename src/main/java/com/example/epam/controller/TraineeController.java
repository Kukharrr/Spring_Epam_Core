package com.example.epam.controller;


import com.example.epam.dto.*;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import com.example.epam.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    public ResponseEntity<Trainee> createTrainee(@RequestBody TraineeCreateDto traineeCreateDto) {
        System.out.println("registering new trainee");
        Trainee trainee = traineeService.createTrainee(traineeCreateDto);
        return ResponseEntity.ok(trainee);
    }

    @GetMapping
    public ResponseEntity<List<Trainee>> getTrainees() {
       List<Trainee> trainees = traineeService.getAllTrainees();
       return ResponseEntity.ok(trainees);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Trainee> getTraineeByUsername(@PathVariable String username) {
        Optional<Trainee> trainee = traineeService.findTraineeByUsername(username);
        return trainee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/match")
    public ResponseEntity<Boolean> matchTraineeCredentials(@RequestBody UserCredentials credentials) {
        boolean match = traineeService.matchTraineeCredentials(credentials.getUsername(), credentials.getPassword());
        return ResponseEntity.ok(match);
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String username, @RequestBody PasswordUpdate passwordUpdate) {
        traineeService.updatePassword(username, passwordUpdate.getPassword());
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Trainee> updateTrainee(@RequestBody TraineeUpdateDto trainee) {
        Trainee updatedTrainee = traineeService.updateTrainee(trainee);
        return ResponseEntity.ok(updatedTrainee);
    }

    @PutMapping("/{username}/activate")
    public ResponseEntity<Void> activateTrainee(@PathVariable String username, @RequestBody ActivationStatus status) {
        traineeService.setActiveStatus(username, status.isActive());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.noContent().build();
    }

//    @PutMapping("/{username}/trainers")
//    public ResponseEntity<Void> updateTrainerList(@PathVariable String username, @RequestBody Collection<Trainer> trainers) {
//        Set<Trainer> trainerSet = new HashSet<>(trainers);
//        traineeService.updateTrainerList(username, trainerSet);
//        return ResponseEntity.noContent().build();
//    }
}
