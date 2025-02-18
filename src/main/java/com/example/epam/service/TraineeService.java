package com.example.epam.service;

import com.example.epam.dao.UserDao;
import com.example.epam.dto.TraineeCreateDto;
import com.example.epam.dto.TraineeUpdateDto;
import com.example.epam.entity.Trainee;
import com.example.epam.entity.User;
import com.example.epam.util.UsernamePasswordGenerator;
import com.example.epam.dao.TraineeDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDAO;
    private final UserDao userDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;
    private final SessionFactory sessionFactory;

    @Autowired
    public TraineeService(TraineeDao traineeDAO, UserDao userDao, UsernamePasswordGenerator usernamePasswordGenerator, SessionFactory sessionFactory) {
        this.traineeDAO = traineeDAO;
        this.userDao = userDao;
        this.usernamePasswordGenerator = usernamePasswordGenerator;
        this.sessionFactory = sessionFactory;
    }

    public Trainee createTrainee(TraineeCreateDto traineeCreateDto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String username = usernamePasswordGenerator.generateUniqueUsername(traineeCreateDto.getFirstName(), traineeCreateDto.getLastName(), traineeDAO::findByUsername);
            String password = UsernamePasswordGenerator.generatePassword();
            Trainee trainee = new Trainee();
            User user = new User();
            user.setPassword(password);
            user.setUsername(username);
            user.setFirstName(traineeCreateDto.getFirstName());
            user.setLastName(traineeCreateDto.getLastName());
            user.setActive(true);
            userDao.save(user);

            trainee.setUser(user);
            trainee.setAddress(traineeCreateDto.getAddress());
            trainee.setDateOfBirth(traineeCreateDto.getDateOfBirth());
            traineeDAO.save(trainee);

            transaction.commit();

            logger.info("Created trainee with username: {}", trainee.getUser().getUsername());

            return trainee;
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

    public Optional<Trainee> findTraineeByUsername(String username) {
        return traineeDAO.findByUsername(username);
    }

    public void deleteTrainee(String username) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            traineeDAO.deleteByUsername(username);

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

    public Trainee updateTrainee(TraineeUpdateDto traineeUpdateDto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<Trainee> existingTraineeOpt = traineeDAO.findByUsername(traineeUpdateDto.getUsername());
            existingTraineeOpt.ifPresent(existingTrainee -> {
                User existingUser = existingTrainee.getUser();
                Optional.ofNullable(traineeUpdateDto.getFirstName()).ifPresent(existingUser::setFirstName);
                Optional.ofNullable(traineeUpdateDto.getLastName()).ifPresent(existingUser::setLastName);
                Optional.ofNullable(traineeUpdateDto.getPassword()).ifPresent(existingUser::setPassword);
                existingUser.setActive(traineeUpdateDto.getIsActive());
                userDao.update(existingUser);

                existingTrainee.setUser(existingUser);
                Optional.ofNullable(traineeUpdateDto.getDateOfBirth()).ifPresent(existingTrainee::setDateOfBirth);
                Optional.ofNullable(traineeUpdateDto.getAddress()).ifPresent(existingTrainee::setAddress);

                traineeDAO.updateTrainee(existingTrainee);
            });

            transaction.commit();

            return existingTraineeOpt.get();
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

    public List<Trainee> getAllTrainees() {
        return traineeDAO.getAll();
    }

    public boolean matchTraineeCredentials(String username, String password) {
        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        return traineeOpt.isPresent() && traineeOpt.get().getUser().getPassword().equals(password);
    }

    public void updatePassword(String username, String newPassword) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
            traineeOpt.ifPresent(trainee -> {
                trainee.getUser().setPassword(newPassword);
                traineeDAO.updateTrainee(trainee);
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

    public void setActiveStatus(String username, boolean isActive) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
            traineeOpt.ifPresent(trainee -> {
                trainee.getUser().setActive(isActive);
                traineeDAO.updateTrainee(trainee);
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
}
