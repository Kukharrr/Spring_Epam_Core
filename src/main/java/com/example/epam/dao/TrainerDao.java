package com.example.epam.dao;

import com.example.epam.entity.Trainer;
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
public class TrainerDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDao.class);
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(trainer);
            session.getTransaction().commit();
            logger.info("Trainer saved: {}", trainer);
        } catch (Exception e) {
            logger.error("Error saving trainer: {}", trainer, e);
            throw e;
        }
    }

    public Optional<Trainer> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Getting trainer by ID: {}", id);
            return Optional.ofNullable(session.get(Trainer.class, id));
        } catch (Exception e) {
            logger.error("Error getting trainer by ID: {}", id, e);
            throw e;
        }
    }

    public List<Trainer> getAll() {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Getting all trainers");
            Query<Trainer> query = session.createQuery("FROM Trainer", Trainer.class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting all trainers", e);
            throw e;
        }
    }

    public Optional<Trainer> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            logger.info("Finding trainer by username: {}", username);
            Query<Trainer> query = session.createQuery(
                    "SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding trainer by username: {}", username, e);
            throw e;
        }
    }

    public void deleteByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            logger.info("Deleting trainer by username: {}", username);
            Query<?> query = session.createQuery("DELETE FROM Trainer t WHERE t.user.username = :username");
            query.setParameter("username", username);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting trainer by username: {}", username, e);
            throw e;
        }
    }

    public void updateTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(trainer);
            session.getTransaction().commit();
            logger.info("Trainer updated: {}", trainer);
        } catch (Exception e) {
            logger.error("Error updating trainer: {}", trainer, e);
            throw e;
        }
    }
}
