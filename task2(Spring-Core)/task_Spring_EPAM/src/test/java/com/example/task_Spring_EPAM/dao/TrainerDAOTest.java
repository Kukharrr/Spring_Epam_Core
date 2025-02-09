package com.example.task_Spring_EPAM.dao;

import com.example.task_Spring_EPAM.entity.Trainer;
import com.example.task_Spring_EPAM.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDAOTest {

    private TrainerDAO trainerDAO;
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
        trainerDAO = new TrainerDAO();
        trainerDAO.setStorage(storage);
    }

    @Test
    void testSaveAndFindByUsername() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alice.smith");
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");

        trainerDAO.save(trainer);
        Optional<Trainer> found = trainerDAO.findByUsername("alice.smith");

        assertTrue(found.isPresent());
        assertEquals("alice.smith", found.get().getUsername());
        assertEquals("Alice", found.get().getFirstName());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<Trainer> found = trainerDAO.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByUsername() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alice.smith");

        trainerDAO.save(trainer);
        trainerDAO.deleteByUsername("alice.smith");
        Optional<Trainer> found = trainerDAO.findByUsername("alice.smith");

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alice.smith");
        trainer.setFirstName("Alice");

        trainerDAO.save(trainer);

        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setUsername("alice.smith");
        updatedTrainer.setFirstName("Alicia");

        trainerDAO.updateTrainer(updatedTrainer);

        Optional<Trainer> found = trainerDAO.findByUsername("alice.smith");

        assertTrue(found.isPresent());
        assertEquals("Alicia", found.get().getFirstName());
    }

    @Test
    void testGetAllTrainers() {
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("alice.smith");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("bob.johnson");

        trainerDAO.save(trainer1);
        trainerDAO.save(trainer2);

        Collection<Trainer> trainers = trainerDAO.getAllTrainers();

        assertEquals(2, trainers.size());
        assertTrue(trainers.contains(trainer1));
        assertTrue(trainers.contains(trainer2));
    }
}
