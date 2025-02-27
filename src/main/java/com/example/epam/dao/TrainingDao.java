package com.example.epam.dao;

import com.example.epam.entity.Training;
import com.example.epam.entity.TrainingType;
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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public class TrainingDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainingDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Training training, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Saving training in DAO, transactionId: {}, trainingName: {}", transactionId, training.getTrainingName());
        session.save(training);
    }

    @SuppressWarnings("unchecked")
    public Collection<Training> findByTraineeWithRelations(String traineeUsername, LocalDate from, LocalDate to, String trainerName, String trainingType, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding trainings by trainee with relations, transactionId: {}, traineeUsername: {}, from: {}, to: {}, trainerName: {}, trainingType: {}", transactionId, traineeUsername, from, to, trainerName, trainingType);
        Query<Training> query = session.createQuery("SELECT t FROM Training t LEFT JOIN FETCH t.trainer LEFT JOIN FETCH t.trainee LEFT JOIN FETCH t.trainingType WHERE t.trainee.user.username = :traineeUsername" +
                (from != null ? " AND t.trainingDate >= :from" : "") +
                (to != null ? " AND t.trainingDate <= :to" : "") +
                (trainerName != null ? " AND t.trainer.user.username = :trainerName" : "") +
                (trainingType != null ? " AND t.trainingType.trainingTypeName = :trainingType" : ""));
        query.setParameter("traineeUsername", traineeUsername);
        if (from != null) query.setParameter("from", from);
        if (to != null) query.setParameter("to", to);
        if (trainerName != null) query.setParameter("trainerName", trainerName);
        if (trainingType != null) query.setParameter("trainingType", trainingType);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Collection<Training> findByTrainerWithRelations(String trainerUsername, LocalDate from, LocalDate to, String traineeName, Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Finding trainings by trainer with relations, transactionId: {}, trainerUsername: {}, from: {}, to: {}, traineeName: {}", transactionId, trainerUsername, from, to, traineeName);
        Query<Training> query = session.createQuery("SELECT t FROM Training t LEFT JOIN FETCH t.trainer LEFT JOIN FETCH t.trainee LEFT JOIN FETCH t.trainingType WHERE t.trainer.user.username = :trainerUsername" +
                (from != null ? " AND t.trainingDate >= :from" : "") +
                (to != null ? " AND t.trainingDate <= :to" : "") +
                (traineeName != null ? " AND t.trainee.user.username = :traineeName" : ""));
        query.setParameter("trainerUsername", trainerUsername);
        if (from != null) query.setParameter("from", from);
        if (to != null) query.setParameter("to", to);
        if (traineeName != null) query.setParameter("traineeName", traineeName);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TrainingType> getAllTrainingTypes(Session session) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting all training types, transactionId: {}", transactionId);
        return session.createQuery("SELECT tt FROM TrainingType tt").getResultList();
    }
}