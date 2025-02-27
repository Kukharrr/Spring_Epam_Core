package com.example.epam.service;

import com.example.epam.dao.TrainingDao;
import com.example.epam.dao.TrainerDao;
import com.example.epam.dao.TraineeDao;
import com.example.epam.entity.Training;
import com.example.epam.entity.TrainingType;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.Trainee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);
    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingService(TrainingDao trainingDao, TrainerDao trainerDao, TraineeDao traineeDao, SessionFactory sessionFactory) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.sessionFactory = sessionFactory;
    }

    public void createTraining(Training training, String trainerUsername) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Creating training, transactionId: {}, trainerUsername: {}, training: {}", transactionId, trainerUsername, training);
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Trainer trainer = trainerDao.findByUsername(trainerUsername, session).orElseThrow(() -> new RuntimeException("Trainer not found"));
            Trainee trainee = traineeDao.findByUsername(training.getTrainee().getUser().getUsername(), session).orElseThrow(() -> new RuntimeException("Trainee not found"));
            training.setTrainer(trainer);
            training.setTrainee(trainee);
            trainingDao.save(training, session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error creating training, transactionId: {}, trainerUsername: {}, error: {}", transactionId, trainerUsername, e.getMessage());
            throw new RuntimeException("Failed to create training", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Collection<Training> getTrainingsByTraineeWithRelations(String traineeUsername, LocalDate from, LocalDate to, String trainerName, String trainingType) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting trainings by trainee with relations, transactionId: {}, traineeUsername: {}, from: {}, to: {}, trainerName: {}, trainingType: {}", transactionId, traineeUsername, from, to, trainerName, trainingType);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Collection<Training> trainings = trainingDao.findByTraineeWithRelations(traineeUsername, from, to, trainerName, trainingType, session);
            return trainings;
        } catch (Exception e) {
            logger.error("Error getting trainings by trainee with relations, transactionId: {}, traineeUsername: {}, error: {}", transactionId, traineeUsername, e.getMessage());
            throw new RuntimeException("Failed to get trainings by trainee with relations", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Collection<Training> getTrainingsByTrainer(String trainerUsername, LocalDate from, LocalDate to, String traineeName) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting trainings by trainer, transactionId: {}, trainerUsername: {}, from: {}, to: {}, traineeName: {}", transactionId, trainerUsername, from, to, traineeName);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Collection<Training> trainings = trainingDao.findByTrainerWithRelations(trainerUsername, from, to, traineeName, session);
            return trainings;
        } catch (Exception e) {
            logger.error("Error getting trainings by trainer, transactionId: {}, trainerUsername: {}, error: {}", transactionId, trainerUsername, e.getMessage());
            throw new RuntimeException("Failed to get trainings by trainer", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<TrainingType> getAllTrainingTypes() {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Getting all training types, transactionId: {}", transactionId);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<TrainingType> types = trainingDao.getAllTrainingTypes(session);
            return types;
        } catch (Exception e) {
            logger.error("Error getting all training types, transactionId: {}, error: {}", transactionId, e.getMessage());
            throw new RuntimeException("Failed to get all training types", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}