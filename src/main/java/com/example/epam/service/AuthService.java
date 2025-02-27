package com.example.epam.service;

import com.example.epam.dao.TraineeDao;
import com.example.epam.dao.TrainerDao;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final SessionFactory sessionFactory;

    @Autowired
    public AuthService(TraineeDao traineeDao, TrainerDao trainerDao, SessionFactory sessionFactory) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.sessionFactory = sessionFactory;
    }

    public boolean authenticateTrainee(String username, String password) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Authenticating trainee, transactionId: {}, username: {}", transactionId, username);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            if (password == null || password.isEmpty()) {
                logger.error("Authentication failed, missing password, transactionId: {}, username: {}", transactionId, username);
                return false;
            }
            return traineeDao.findByUsername(username, session)
                    .map(trainee -> trainee.getUser().getPassword().equals(password) && trainee.getUser().getIsActive())
                    .orElse(false);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean authenticateTrainer(String username, String password) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Authenticating trainer, transactionId: {}, username: {}", transactionId, username);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            if (password == null || password.isEmpty()) {
                logger.error("Authentication failed, missing password, transactionId: {}, username: {}", transactionId, username);
                return false;
            }
            return trainerDao.findByUsername(username, session)
                    .map(trainer -> trainer.getUser().getPassword().equals(password) && trainer.getUser().getIsActive())
                    .orElse(false);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}