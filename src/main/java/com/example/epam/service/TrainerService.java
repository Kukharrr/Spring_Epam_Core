package com.example.epam.service;

import com.example.epam.dao.TrainerDao;
import com.example.epam.dao.UserDao;
import com.example.epam.dto.TrainerCreateDto;
import com.example.epam.dto.TrainerUpdateDto;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.User;
import com.example.epam.util.UsernamePasswordGenerator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDao trainerDAO;
    private final UserDao userDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;
    private final SessionFactory sessionFactory;

    @Autowired
    public TrainerService(TrainerDao trainerDAO, UserDao userDao, UsernamePasswordGenerator usernamePasswordGenerator, SessionFactory sessionFactory) {
        this.trainerDAO = trainerDAO;
        this.userDao = userDao;
        this.usernamePasswordGenerator = usernamePasswordGenerator;
        this.sessionFactory = sessionFactory;
    }

    public Trainer createTrainer(TrainerCreateDto trainerCreateDto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String username = usernamePasswordGenerator.generateUniqueUsername(trainerCreateDto.getFirstName(), trainerCreateDto.getLastName(), trainerDAO::findByUsername);
            String password = UsernamePasswordGenerator.generatePassword();
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(trainerCreateDto.getFirstName());
            user.setLastName(trainerCreateDto.getLastName());
            user.setActive(true);
            userDao.save(user);
            Trainer trainer = new Trainer();
            trainer.setUser(user);
            trainer.setSpecialization(trainerCreateDto.getSpecialization());
            trainerDAO.save(trainer);

            transaction.commit();

            logger.info("Created trainer with username: {}", user.getUsername());

            return trainer;
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

    public Optional<Trainer> findTrainerByUsername(String username) {
        return trainerDAO.findByUsername(username);
    }

    public void deleteTrainer(String username) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            trainerDAO.deleteByUsername(username);

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

    public Trainer updateTrainer(TrainerUpdateDto trainerUpdateDto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<Trainer> existingTrainerOpt = trainerDAO.findByUsername(trainerUpdateDto.getUsername());
            existingTrainerOpt.ifPresent(existingTrainer -> {
                User existingUser = existingTrainer.getUser();
                Optional.ofNullable(trainerUpdateDto.getFirstName()).ifPresent(existingUser::setFirstName);
                Optional.ofNullable(trainerUpdateDto.getLastName()).ifPresent(existingUser::setLastName);
                Optional.ofNullable(trainerUpdateDto.getPassword()).ifPresent(existingUser::setPassword);
                existingUser.setActive(trainerUpdateDto.getIsActive());
                userDao.update(existingUser);

                existingTrainer.setUser(existingUser);
                Optional.ofNullable(trainerUpdateDto.getSpecialization()).ifPresent(existingTrainer::setSpecialization);
                existingTrainer.getUser().setActive(trainerUpdateDto.getIsActive());
                trainerDAO.updateTrainer(existingTrainer);
            });

            transaction.commit();

            return existingTrainerOpt.get();
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

    public List<Trainer> getAllTrainers() {
        return trainerDAO.getAll();
    }

    public boolean matchTrainerCredentials(String username, String password) {
        Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
        return trainerOpt.isPresent() && trainerOpt.get().getUser().getPassword().equals(password);
    }

    public void updatePassword(String username, String newPassword) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
            trainerOpt.ifPresent(trainer -> {
                trainer.getUser().setPassword(newPassword);
                trainerDAO.updateTrainer(trainer);
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

            Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
            trainerOpt.ifPresent(trainer -> {
                trainer.getUser().setActive(isActive);
                trainerDAO.updateTrainer(trainer);
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
