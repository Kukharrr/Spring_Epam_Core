package com.example.epam.dao;

import com.example.epam.entity.Trainee;
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
public class TraineeDao {
    private static final Logger logger = LoggerFactory.getLogger(TraineeDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public TraineeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainee trainee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (trainee.getId() == null) {
                session.save(trainee);
                logger.info("Trainee saved: {}", trainee);
            } else {
                session.update(trainee);
                logger.info("Trainee updated: {}", trainee);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error saving or updating trainee: {}", trainee, e);
            throw e;
        }
    }

    public Optional<Trainee> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Getting trainee by ID: {}", id);
            return Optional.ofNullable(session.get(Trainee.class, id));
        } catch (Exception e) {
            logger.error("Error getting trainee by ID: {}", id, e);
            throw e;
        }
    }

    public List<Trainee> getAll() {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Getting all trainees");
            Query<Trainee> query = session.createQuery("FROM Trainee", Trainee.class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting all trainees", e);
            throw e;
        }
    }

    public Optional<Trainee> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Finding trainee by username: {}", username);
            Query<Trainee> query = session.createQuery(
                    "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username", Trainee.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding trainee by username: {}", username, e);
            throw e;
        }
    }

    public void deleteByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            logger.info("Deleting trainee by username: {}", username);
            Query<?> query = session.createQuery("DELETE FROM Trainee t WHERE t.user.username = :username");
            query.setParameter("username", username);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting trainee by username: {}", username, e);
            throw e;
        }
    }

    public void updateTrainee(Trainee trainee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(trainee);
            session.getTransaction().commit();
            logger.info("Trainee updated: {}", trainee);
        } catch (Exception e) {
            logger.error("Error updating trainee: {}", trainee, e);
            throw e;
        }
    }
}
