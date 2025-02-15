package com.example.epam.service;


import com.example.epam.dao.TrainerDao;
import com.example.epam.dao.UserDao;
import com.example.epam.dto.TraineeCreateDto;
import com.example.epam.dto.TrainerCreateDto;
import com.example.epam.dto.TrainerUpdateDto;
import com.example.epam.entity.Trainer;
import com.example.epam.entity.User;
import com.example.epam.util.UsernamePasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDao trainerDAO;
    private final UserDao userDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;

    @Autowired
    public TrainerService(TrainerDao trainerDAO,UserDao userDao, UsernamePasswordGenerator usernamePasswordGenerator) {
        this.trainerDAO = trainerDAO;
        this.userDao = userDao;
        this.usernamePasswordGenerator = usernamePasswordGenerator;
    }

    public Trainer createTrainer(TrainerCreateDto trainerCreateDto) {
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
        logger.info("Created trainer with username: {}", user.getUsername());

        return trainer;
    }

    public Optional<Trainer> findTrainerByUsername(String username) {
        logger.info("Finding trainer by username: {}", username);
        return trainerDAO.findByUsername(username);
    }

    public void deleteTrainer(String username) {
        logger.info("Deleting trainer by username: {}", username);
        trainerDAO.deleteByUsername(username);
    }

    public Trainer updateTrainer(TrainerUpdateDto trainerUpdateDto) {
        logger.info("Updating trainer with username: {}", trainerUpdateDto.getUsername());
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

        return existingTrainerOpt.get();
    }

    public List<Trainer> getAllTrainers() {
        logger.info("Getting all trainers");
        return trainerDAO.getAll();
    }

    public boolean matchTrainerCredentials(String username, String password) {
        Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
        return trainerOpt.isPresent() && trainerOpt.get().getUser().getPassword().equals(password);
    }

    public void updatePassword(String username, String newPassword) {
        Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
        trainerOpt.ifPresent(trainer -> {
            trainer.getUser().setPassword(newPassword);
            trainerDAO.updateTrainer(trainer);
        });
    }

    public void setActiveStatus(String username, boolean isActive) {
        Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
        trainerOpt.ifPresent(trainer -> {
            trainer.getUser().setActive(isActive);
            trainerDAO.updateTrainer(trainer);
        });
    }

//    public Collection<Trainer> getUnassignedTrainers(String traineeUsername) {
//        return trainerDAO.getAll().stream()
//                .filter(trainer -> trainer.getTrainees().stream().noneMatch(trainee -> trainee.getUser().getUsername().equals(traineeUsername)))
//                .collect(Collectors.toList());
//    }
}
