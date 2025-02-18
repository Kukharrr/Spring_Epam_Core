package com.example.epam.service;

import com.example.epam.dao.UserDao;
import com.example.epam.dto.TraineeCreateDto;
import com.example.epam.dto.TraineeUpdateDto;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.User;
import com.example.epam.util.UsernamePasswordGenerator;
import com.example.epam.dao.TraineeDao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Transactional
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

    @Transactional
    public Optional<Trainee> findTraineeByUsername(String username) {
        return traineeDAO.findByUsername(username);
    }

    @Transactional
    public void deleteTrainee(String username) {
        traineeDAO.deleteByUsername(username);
    }

    @Transactional
    public Trainee updateTrainee(TraineeUpdateDto traineeUpdateDto) {
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
        return traineeDAO.getAll();
    }

    @Transactional
    public boolean matchTraineeCredentials(String username, String password) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        return traineeOpt.isPresent() && traineeOpt.get().getUser().getPassword().equals(password);
    }

    @Transactional
    public void updatePassword(String username, String newPassword) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        traineeOpt.ifPresent(trainee -> {
            trainee.getUser().setPassword(newPassword);
            traineeDAO.updateTrainee(trainee);
        });
    }

    @Transactional
    public void setActiveStatus(String username, boolean isActive) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        traineeOpt.ifPresent(trainee -> {
            trainee.getUser().setActive(isActive);
            traineeDAO.updateTrainee(trainee);
        });
    }
}
