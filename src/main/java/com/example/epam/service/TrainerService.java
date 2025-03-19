package com.example.epam.service;

import com.example.epam.dao.TrainerDao;
import com.example.epam.dao.UserDao;
import com.example.epam.dto.TrainerCreateDto;
import com.example.epam.dto.TrainerUpdateDto;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.Trainee;
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
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TrainerService(TrainerDao trainerDao, UserDao userDao, SessionFactory sessionFactory, PasswordEncoder passwordEncoder) {
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.sessionFactory = sessionFactory;
        this.passwordEncoder = passwordEncoder;
    }

    private <T> T executeInTransaction(SessionFunction<T> function, String operation, String identifier, Object... params) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("{} in TrainerService, transactionId: {}, identifier: {}", operation, transactionId, identifier);
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

    private void executeInTransaction(SessionVoidFunction function, String operation, String identifier, Object... params) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("{} in TrainerService, transactionId: {}, identifier: {}", operation, transactionId, identifier);
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

    public Trainer createTrainer(TrainerCreateDto trainerCreateDto) {
        return executeInTransaction(session -> {
            User user = new User();
            user.setFirstName(trainerCreateDto.getFirstName());
            user.setLastName(trainerCreateDto.getLastName());
            user.setUsername(generateUniqueUsername(trainerCreateDto.getFirstName(), trainerCreateDto.getLastName()));

            String rawPassword = generatePassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRawPassword(rawPassword);

            user.setIsActive(true);
            userDao.save(user, session);

            Trainer trainer = new Trainer();
            trainer.setUser(user);
            trainer.setSpecialization(trainerCreateDto.getSpecialization());
            trainerDao.save(trainer, session);

            logger.info("Generated password for trainer {}: {}", user.getUsername(), rawPassword);
            return trainer;
        }, "create trainer", trainerCreateDto.getFirstName() + " " + trainerCreateDto.getLastName());
    }


    public List<Trainer> getAllTrainersWithTrainees() {
        return executeInTransaction(session -> {
            return trainerDao.findAllWithTrainees(session);
        }, "get all trainers with trainees", "");
    }

    public Trainer getTrainerProfileWithTrainees(String username) {
        return executeInTransaction(session -> {
            Optional<Trainer> trainerOpt = trainerDao.findByUsernameWithTrainees(username, session);
            return trainerOpt.orElseThrow(() -> new RuntimeException("Trainer not found"));
        }, "get trainer profile with trainees", username);
    }

    public void updatePassword(String username, String newPassword) {
        executeInTransaction(session -> {
            Optional<Trainer> trainerOpt = trainerDao.findByUsername(username, session);
            Trainer trainer = trainerOpt.orElseThrow(() -> new RuntimeException("Trainer not found"));

            trainer.getUser().setPassword(passwordEncoder.encode(newPassword));
            userDao.update(trainer.getUser(), session);
        }, "update trainer password", username);
    }

    public Trainer updateTrainer(TrainerUpdateDto trainer) {
        return executeInTransaction(session -> {
            Optional<Trainer> trainerOpt = trainerDao.findByUsername(trainer.getUsername(), session);
            Trainer existingTrainer = trainerOpt.orElseThrow(() -> new RuntimeException("Trainer not found"));
            existingTrainer.getUser().setFirstName(trainer.getFirstName());
            existingTrainer.getUser().setLastName(trainer.getLastName());
            existingTrainer.setSpecialization(trainer.getSpecialization());
            userDao.update(existingTrainer.getUser(), session);
            trainerDao.updateTrainer(existingTrainer, session);
            return existingTrainer;
        }, "update trainer", trainer.getUsername());
    }

    public void setActiveStatus(String username, boolean isActive) {
        executeInTransaction(session -> {
            Optional<Trainer> trainerOpt = trainerDao.findByUsername(username, session);
            Trainer trainer = trainerOpt.orElseThrow(() -> new RuntimeException("Trainer not found"));
            trainer.getUser().setIsActive(isActive);
            userDao.update(trainer.getUser(), session);
        }, "set trainer active status", username, isActive);
    }

    public void deleteTrainer(String username) {
        executeInTransaction(session -> {
            Optional<Trainer> trainerOpt = trainerDao.findByUsername(username, session);
            Trainer trainer = trainerOpt.orElseThrow(() -> new RuntimeException("Trainer not found"));
            trainerDao.delete(trainer.getId(), session);
        }, "delete trainer", username);
    }

    public List<Trainee> getNotAssignedTrainees(String username) {
        return executeInTransaction(session -> {
            Optional<Trainer> trainerOpt = trainerDao.findByUsername(username, session);
            Trainer trainer = trainerOpt.orElseThrow(() -> new RuntimeException("Trainer not found"));
            List<Trainee> allTrainees = userDao.getAllTrainees(session);
            return allTrainees.stream()
                    .filter(trainee -> !trainer.getTrainees().contains(trainee) && trainee.getUser().getIsActive())
                    .collect(Collectors.toList());
        }, "get not assigned trainees", username);
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
