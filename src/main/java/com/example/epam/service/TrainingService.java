package com.example.epam.service;

import com.example.epam.dao.TrainingDao;
import com.example.epam.entity.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);
    private final TrainingDao trainingDAO;
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingService(TrainingDao trainingDAO, SessionFactory sessionFactory) {
        this.trainingDAO = trainingDAO;
        this.sessionFactory = sessionFactory;
    }

    public void createTraining(Training training) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            trainingDAO.save(training);

            transaction.commit();

            logger.info("Created training with ID: {}", training.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Optional<Training> findTrainingById(Long id) {
        return trainingDAO.findById(id);
    }

    public void deleteTraining(Long id) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            trainingDAO.delete(id);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void updateTraining(Training training) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<Training> existingTrainingOpt = trainingDAO.findById(training.getId());
            existingTrainingOpt.ifPresent(existingTraining -> {
                Optional.ofNullable(training.getTrainee()).ifPresent(existingTraining::setTrainee);
                Optional.ofNullable(training.getTrainer()).ifPresent(existingTraining::setTrainer);
                Optional.ofNullable(training.getTrainingName()).ifPresent(existingTraining::setTrainingName);
                Optional.ofNullable(training.getTrainingType()).ifPresent(existingTraining::setTrainingType);
                Optional.ofNullable(training.getTrainingDate()).ifPresent(existingTraining::setTrainingDate);
                existingTraining.setTrainingDuration(training.getTrainingDuration());
                trainingDAO.updateTraining(existingTraining);
            });

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Collection<Training> getAllTrainings() {
        return trainingDAO.getAll();
    }

    public Collection<Training> getTrainingsByTrainee(String traineeUsername, Date startDate, Date endDate, String trainerName, String trainingType) {
        return trainingDAO.getAll().stream()
                .filter(training -> training.getTrainee().getUser().getUsername().equals(traineeUsername))
                .filter(training -> (startDate == null || !training.getTrainingDate().before(startDate)) &&
                        (endDate == null || !training.getTrainingDate().after(endDate)))
                .filter(training -> trainerName == null || training.getTrainer().getUser().getUsername().equals(trainerName))
                .filter(training -> trainingType == null || training.getTrainingType().getTrainingTypeName().equals(trainingType))
                .collect(Collectors.toList());
    }

    public Collection<Training> getTrainingsByTrainer(String trainerUsername, Date startDate, Date endDate, String traineeName) {
        return trainingDAO.getAll().stream()
                .filter(training -> training.getTrainer().getUser().getUsername().equals(trainerUsername))
                .filter(training -> (startDate == null || !training.getTrainingDate().before(startDate)) &&
                        (endDate == null || !training.getTrainingDate().after(endDate)))
                .filter(training -> traineeName == null || training.getTrainee().getUser().getUsername().equals(traineeName))
                .collect(Collectors.toList());
    }
}
