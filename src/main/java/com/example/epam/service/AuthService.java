package com.example.epam.service;

import com.example.epam.dao.TraineeDao;
import com.example.epam.dao.TrainerDao;
import com.example.epam.dao.UserDao;
import com.example.epam.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(TraineeDao traineeDao, TrainerDao trainerDao, UserDao userDao, SessionFactory sessionFactory,
                       PasswordEncoder passwordEncoder) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.sessionFactory = sessionFactory;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String password) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Authenticating user, transactionId: {}, username: {}", transactionId, username);

        try (Session session = sessionFactory.openSession()) {
            Optional<User> userOptional = userDao.findByUsername(username, session);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (passwordEncoder.matches(password, user.getPassword()) && user.getIsActive()) {
                    logger.info("Authentication successful, transactionId: {}, username: {}", transactionId, username);
                    return true;
                }
            }
        }

        logger.warn("Authentication failed, transactionId: {}, username: {}", transactionId, username);
        return false;
    }
}