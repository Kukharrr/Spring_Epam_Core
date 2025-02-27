package com.example.epam.dao;

import com.example.epam.entity.Trainee;
import com.example.epam.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Saving user in DAO, transactionId: {}, username: {}", transactionId, user.getUsername());
        session.save(user);
    }

    public void update(User user, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating user in DAO, transactionId: {}, username: {}", transactionId, user.getUsername());
        session.update(user);
    }

    public Optional<User> findByUsername(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding user by username, transactionId: {}, username: {}", transactionId, username);
        return session.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @SuppressWarnings("unchecked")
    public List<Trainee> getAllTrainees(Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting all trainees, transactionId: {}", transactionId);
        return session.createQuery("SELECT t FROM Trainee t").getResultList();
    }
}