package com.example.epam.dao;

import com.example.epam.entity.Trainee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public TraineeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainee trainee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(trainee);
            session.getTransaction().commit();
        }
    }

    public Optional<Trainee> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Trainee.class, id));
        }
    }

    public List<Trainee> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Trainee ", Trainee.class).list();
        }
    }

    public Optional<Trainee> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<Trainee> query = session.createQuery(
                    "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username", Trainee.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        }
    }

    public void deleteByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Trainee t WHERE t.user.username = :username");
            query.setParameter("username", username);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    public void updateTrainee(Trainee trainee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(trainee);
            session.getTransaction().commit();
        }
    }
}
