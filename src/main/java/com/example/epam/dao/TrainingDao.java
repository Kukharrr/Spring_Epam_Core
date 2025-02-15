package com.example.epam.dao;

import com.example.epam.entity.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDao {
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
        }
    }

    public Optional<Training> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Training.class, id));
        }
    }

    public List<Training> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Training", Training.class).list();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Training training = session.get(Training.class, id);
            if (training != null) {
                session.remove(training);
            }
            session.getTransaction().commit();
        }
    }

    public void updateTraining(Training training) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(training);
            session.getTransaction().commit();
        }
    }
}
