package com.example.epam.dao;

import com.example.epam.dto.TraineeUpdateDto;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TraineeDao {
    private static final Logger logger = LoggerFactory.getLogger(TraineeDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public TraineeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainee trainee, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Saving trainee in DAO, transactionId: {}, username: {}", transactionId, trainee.getUser().getUsername());
        session.save(trainee);
    }

    public Optional<Trainee> findByUsernameWithTrainers(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding trainee by username with trainers, transactionId: {}, username: {}", transactionId, username);
        Query<Trainee> query = session.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.user.username = :username");
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    public Optional<Trainee> findByUsername(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding trainee by username, transactionId: {}, username: {}", transactionId, username);
        Query<Trainee> query = session.createQuery("SELECT t FROM Trainee t WHERE t.user.username = :username");
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    public void updateTrainee(Trainee trainee, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainee in DAO, transactionId: {}, username: {}", transactionId, trainee.getUser().getUsername());
        session.update(trainee);
    }

    public void updatePassword(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainee password in DAO, transactionId: {}, username: {}", transactionId, username);
        Trainee trainee = findByUsername(username, session).orElseThrow(() -> new RuntimeException("Trainee not found"));
        session.update(trainee);
    }

    public void setActiveStatus(String username, boolean isActive, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Setting trainee active status in DAO, transactionId: {}, username: {}, isActive: {}", transactionId, username, isActive);
        Trainee trainee = findByUsername(username, session).orElseThrow(() -> new RuntimeException("Trainee not found"));
        trainee.getUser().setIsActive(isActive);
        session.update(trainee);
    }

    public void delete(Long id, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Deleting trainee in DAO, transactionId: {}, id: {}", transactionId, id);
        Trainee trainee = session.get(Trainee.class, id);
        if (trainee != null) {
            session.delete(trainee);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Trainer> getAll(Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting all trainers, transactionId: {}", transactionId);
        return session.createQuery("SELECT t FROM Trainer t").getResultList();
    }
}