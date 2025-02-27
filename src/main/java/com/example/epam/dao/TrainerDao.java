package com.example.epam.dao;

import com.example.epam.dto.TrainerUpdateDto;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.Trainee;
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
public class TrainerDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainer trainer, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Saving trainer in DAO, transactionId: {}, username: {}", transactionId, trainer.getUser().getUsername());
        session.save(trainer);
    }

    @SuppressWarnings("unchecked")
    public List<Trainer> findAllWithTrainees(Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding all trainers with trainees, transactionId: {}", transactionId);
        Query<Trainer> query = session.createQuery("SELECT t FROM Trainer t LEFT JOIN FETCH t.trainees");
        return query.getResultList();
    }

    public Optional<Trainer> findByUsernameWithTrainees(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding trainer by username with trainees, transactionId: {}, username: {}", transactionId, username);
        Query<Trainer> query = session.createQuery("SELECT t FROM Trainer t LEFT JOIN FETCH t.trainees WHERE t.user.username = :username");
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    public Optional<Trainer> findByUsername(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding trainer by username, transactionId: {}, username: {}", transactionId, username);
        Query<Trainer> query = session.createQuery("SELECT t FROM Trainer t WHERE t.user.username = :username");
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    public void updateTrainer(Trainer trainer, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainer in DAO, transactionId: {}, username: {}", transactionId, trainer.getUser().getUsername());
        session.update(trainer);
    }

    public void updatePassword(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Updating trainer password in DAO, transactionId: {}, username: {}", transactionId, username);
        Trainer trainer = findByUsername(username, session).orElseThrow(() -> new RuntimeException("Trainer not found"));
        session.update(trainer);
    }

    public void setActiveStatus(String username, boolean isActive, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Setting trainer active status in DAO, transactionId: {}, username: {}, isActive: {}", transactionId, username, isActive);
        Trainer trainer = findByUsername(username, session).orElseThrow(() -> new RuntimeException("Trainer not found"));
        trainer.getUser().setIsActive(isActive);
        session.update(trainer);
    }

    public void delete(Long id, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Deleting trainer in DAO, transactionId: {}, id: {}", transactionId, id);
        Trainer trainer = session.get(Trainer.class, id);
        if (trainer != null) {
            session.delete(trainer);
        }
    }

    public List<Trainee> findNotAssignedTrainees(String username, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding not assigned trainees in DAO, transactionId: {}, username: {}", transactionId, username);
        Trainer trainer = findByUsername(username, session).orElseThrow(() -> new RuntimeException("Trainer not found"));
        Query<Trainee> query = session.createQuery("SELECT t FROM Trainee t WHERE t NOT IN (SELECT tt FROM Trainer tr JOIN tr.trainees tt WHERE tr.user.username = :username) AND t.user.isActive = true");
        query.setParameter("username", username);
        return query.getResultList();
    }
}