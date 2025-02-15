package com.example.epam.dao;

import com.example.epam.entity.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao {
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
        }
    }

    public Optional<Trainer> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Trainer.class, id));
        }
    }

    public List<Trainer> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Trainer", Trainer.class).list();
        }
    }

    public Optional<Trainer> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<Trainer> query = session.createQuery(
                    "SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        }
    }

    public void deleteByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Trainer t WHERE t.user.username = :username");
            query.setParameter("username", username);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    public void updateTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(trainer);
            session.getTransaction().commit();
        }
    }
}
