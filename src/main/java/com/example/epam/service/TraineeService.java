package com.example.epam.service;

import com.example.epam.dao.TraineeDao;
import com.example.epam.dao.TrainerDao;
import com.example.epam.dao.UserDao;
import com.example.epam.dto.TraineeCreateDto;
import com.example.epam.dto.TraineeUpdateDto;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDao;
    private final UserDao userDao;
    private final TrainerDao trainerDao;
    private final SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TraineeService(TraineeDao traineeDao, UserDao userDao, TrainerDao trainerDao,
                          SessionFactory sessionFactory, PasswordEncoder passwordEncoder) {
        this.traineeDao = traineeDao;
        this.userDao = userDao;
        this.trainerDao = trainerDao;
        this.sessionFactory = sessionFactory;
        this.passwordEncoder = passwordEncoder;
    }

    private <T> T executeInTransaction(SessionFunction<T> function, String operation, String identifier) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("{} in TraineeService, transactionId: {}, identifier: {}", operation, transactionId, identifier);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = function.apply(session);
                transaction.commit();
                logger.info("{} successful, transactionId: {}, identifier: {}, response: 200", operation, transactionId, identifier);
                return result;
            } catch (Exception e) {
                transaction.rollback();
                logger.error("{} failed, transactionId: {}, identifier: {}, error: {}", operation, transactionId, identifier, e.getMessage());
                throw new RuntimeException("Failed to " + operation, e);
            }
        }
    }

    private void executeInTransaction(SessionVoidFunction function, String operation, String identifier) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("{} in TraineeService, transactionId: {}, identifier: {}", operation, transactionId, identifier);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                function.apply(session);
                transaction.commit();
                logger.info("{} successful, transactionId: {}, identifier: {}, response: 200", operation, transactionId, identifier);
            } catch (Exception e) {
                transaction.rollback();
                logger.error("{} failed, transactionId: {}, identifier: {}, error: {}", operation, transactionId, identifier, e.getMessage());
                throw new RuntimeException("Failed to " + operation, e);
            }
        }
    }

    public Trainee createTrainee(TraineeCreateDto traineeCreateDto) {
        return executeInTransaction(session -> {
            User user = createUserForTrainee(traineeCreateDto, session);

            Trainee trainee = createTraineeEntity(traineeCreateDto, user, session);

            logger.info("Generated password for trainee {}: {}", user.getUsername(), user.getRawPassword());
            return trainee;
        }, "create trainee", traineeCreateDto.getFirstName() + " " + traineeCreateDto.getLastName());
    }

    private User createUserForTrainee(TraineeCreateDto traineeCreateDto, Session session) {
        User user = new User();
        user.setFirstName(traineeCreateDto.getFirstName());
        user.setLastName(traineeCreateDto.getLastName());
        user.setUsername(generateUniqueUsername(traineeCreateDto.getFirstName(), traineeCreateDto.getLastName()));

        String rawPassword = generatePassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsActive(true);

        userDao.save(user, session);

        user.setRawPassword(rawPassword);
        return user;
    }

    private Trainee createTraineeEntity(TraineeCreateDto traineeCreateDto, User user, Session session) {
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(traineeCreateDto.getDateOfBirth());
        trainee.setAddress(traineeCreateDto.getAddress());
        traineeDao.save(trainee, session);
        return trainee;
    }


    public Trainee getTraineeProfileWithTrainers(String username) {
        return executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsernameWithTrainers(username, session);
            return traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));
        }, "get trainee profile with trainers", username);
    }

    public void updatePassword(String username, String newPassword) {
        executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsername(username, session);
            Trainee trainee = traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));

            trainee.getUser().setPassword(passwordEncoder.encode(newPassword));
            userDao.update(trainee.getUser(), session);
        }, "update trainee password", username);
    }

    public Trainee updateTrainee(TraineeUpdateDto traineeUpdateDto) {
        return executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsername(traineeUpdateDto.getUsername(), session);
            Trainee existingTrainee = traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));
            existingTrainee.getUser().setFirstName(traineeUpdateDto.getFirstName());
            existingTrainee.getUser().setLastName(traineeUpdateDto.getLastName());
            existingTrainee.setDateOfBirth(traineeUpdateDto.getDateOfBirth());
            existingTrainee.setAddress(traineeUpdateDto.getAddress());
            userDao.update(existingTrainee.getUser(), session);
            traineeDao.updateTrainee(existingTrainee, session);
            return existingTrainee;
        }, "update trainee", traineeUpdateDto.getUsername());
    }

    public void setActiveStatus(String username, boolean isActive) {
        executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsername(username, session);
            Trainee trainee = traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));
            trainee.getUser().setIsActive(isActive);
            userDao.update(trainee.getUser(), session);
        }, "set trainee active status", username);
    }

    public void deleteTrainee(String username) {
        executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsername(username, session);
            Trainee trainee = traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));
            traineeDao.delete(trainee.getId(), session);
        }, "delete trainee", username);
    }

    public List<Trainer> getNotAssignedTrainers(String username) {
        return executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsername(username, session);
            Trainee trainee = traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));
            List<Trainer> allTrainers = trainerDao.findAllWithTrainees(session);
            return allTrainers.stream()
                    .filter(trainer -> !trainee.getTrainers().contains(trainer) && trainer.getUser().getIsActive())
                    .collect(Collectors.toList());
        }, "get not assigned trainers", username);
    }

    public void updateTrainersList(String username, List<String> trainerUsernames) {
        executeInTransaction(session -> {
            Optional<Trainee> traineeOpt = traineeDao.findByUsername(username, session);
            Trainee trainee = traineeOpt.orElseThrow(() -> new RuntimeException("Trainee not found"));
            List<Trainer> trainers = trainerUsernames.stream()
                    .map(trainerUsername -> trainerDao.findByUsername(trainerUsername, session)
                            .orElseThrow(() -> new RuntimeException("Trainer not found: " + trainerUsername)))
                    .collect(Collectors.toList());
            trainee.setTrainers(trainers);
            traineeDao.updateTrainee(trainee, session);
        }, "update trainee trainers list", username);
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = (firstName + "." + lastName).toLowerCase();
        String username = baseUsername;
        int suffix = 1;
        try (Session session = sessionFactory.openSession()) {
            while (userDao.findByUsername(username, session).isPresent()) {
                username = baseUsername + suffix++;
            }
        }
        return username;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @FunctionalInterface
    private interface SessionFunction<T> {
        T apply(Session session);
    }

    @FunctionalInterface
    private interface SessionVoidFunction {
        void apply(Session session);
    }
}
