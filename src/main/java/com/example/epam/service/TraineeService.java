package com.example.epam.service;


import com.example.epam.dao.UserDao;
import com.example.epam.dto.TraineeCreateDto;
import com.example.epam.dto.TraineeUpdateDto;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.User;
import com.example.epam.util.UsernamePasswordGenerator;
import com.example.epam.dao.TraineeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDAO;
    private final UserDao userDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;

    @Autowired
    public TraineeService(TraineeDao traineeDAO, UserDao userDao, UsernamePasswordGenerator usernamePasswordGenerator) {
        this.traineeDAO = traineeDAO;
        this.userDao = userDao;
        this.usernamePasswordGenerator = usernamePasswordGenerator;
    }

    public Trainee createTrainee(TraineeCreateDto traineeCreateDto) {
        String username = usernamePasswordGenerator.generateUniqueUsername(traineeCreateDto.getFirstName(), traineeCreateDto.getLastName(), traineeDAO::findByUsername);
        String password = UsernamePasswordGenerator.generatePassword();
        Trainee trainee = new Trainee();
        User user = new User();
        user.setPassword(username);
        user.setUsername(password);
        user.setFirstName(traineeCreateDto.getFirstName());
        user.setLastName(traineeCreateDto.getLastName());
        user.setActive(true);
        userDao.save(user);

        trainee.setUser(user);
        trainee.setAddress(traineeCreateDto.getAddress());
        trainee.setDateOfBirth(traineeCreateDto.getDateOfBirth());
        traineeDAO.save(trainee);
        logger.info("Created trainee with username: {}", trainee.getUser().getUsername());

        return trainee;
    }

    public Optional<Trainee> findTraineeByUsername(String username) {
        logger.info("Finding trainee by username: {}", username);
        return traineeDAO.findByUsername(username);
    }

    public void deleteTrainee(String username) {
        logger.info("Deleting trainee by username: {}", username);
        traineeDAO.deleteByUsername(username);
    }

    public Trainee updateTrainee(TraineeUpdateDto traineeUpdateDto) {
        logger.info("Updating trainee with username: {}", traineeUpdateDto.getUsername());
        Optional<Trainee> existingTraineeOpt = traineeDAO.findByUsername(traineeUpdateDto.getUsername());
        existingTraineeOpt.ifPresent(existingTrainee -> {
            User existingUser = existingTrainee.getUser();
            Optional.ofNullable(traineeUpdateDto.getFirstName()).ifPresent(existingUser::setFirstName);
            Optional.ofNullable(traineeUpdateDto.getLastName()).ifPresent(existingUser::setLastName);
            Optional.ofNullable(traineeUpdateDto.getPassword()).ifPresent(existingUser::setPassword);
            existingUser.setActive(traineeUpdateDto.getIsActive());
            userDao.update(existingUser);

            existingTrainee.setUser(existingUser);
            Optional.ofNullable(traineeUpdateDto.getDateOfBirth()).ifPresent(existingTrainee::setDateOfBirth);
            Optional.ofNullable(traineeUpdateDto.getAddress()).ifPresent(existingTrainee::setAddress);

            traineeDAO.updateTrainee(existingTrainee);
        });
        return existingTraineeOpt.get();
    }

    public List<Trainee> getAllTrainees() {
        logger.info("Getting all trainees");
        return traineeDAO.getAll();
    }

    public boolean matchTraineeCredentials(String username, String password) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        return traineeOpt.isPresent() && traineeOpt.get().getUser().getPassword().equals(password);
    }

    public void updatePassword(String username, String newPassword) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        traineeOpt.ifPresent(trainee -> {
            trainee.getUser().setPassword(newPassword);
            traineeDAO.updateTrainee(trainee);
        });
    }

    public void setActiveStatus(String username, boolean isActive) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        traineeOpt.ifPresent(trainee -> {
            trainee.getUser().setActive(isActive);
            traineeDAO.updateTrainee(trainee);
        });
    }

//    public void updateTrainerList(String traineeUsername, Set<Trainer> trainers) {
//        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(traineeUsername);
//        if (traineeOpt.isPresent()) {
//            Trainee trainee = traineeOpt.get();
//            trainee.setTrainers(trainers);
//            traineeDAO.updateTrainee(trainee);
//        }
//    }

}
