package com.example.epam.dao;

import com.example.epam.entity.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainingDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Training training) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(training);
            session.getTransaction().commit();
            logger.info("Training saved: {}", training);
        } catch (Exception e) {
            logger.error("Error saving training: {}", training, e);
            throw e;
        }
    }

    public Optional<Training> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Getting training by ID: {}", id);
            return Optional.ofNullable(session.get(Training.class, id));
        } catch (Exception e) {
            logger.error("Error getting training by ID: {}", id, e);
            throw e;
        }
    }

    public List<Training> getAll() {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Getting all trainings");
            Query<Training> query = session.createQuery("FROM Training", Training.class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting all trainings", e);
            throw e;
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            logger.info("Deleting training by ID: {}", id);
            Training training = session.get(Training.class, id);
            if (training != null) {
                session.remove(training);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting training by ID: {}", id, e);
            throw e;
        }
    }

    public void updateTraining(Training training) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(training);
            session.getTransaction().commit();
            logger.info("Training updated: {}", training);
        } catch (Exception e) {
            logger.error("Error updating training: {}", training, e);
            throw e;
        }
    }
}
