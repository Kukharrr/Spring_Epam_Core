package com.example.task_Spring_EPAM.dao;

import com.example.task_Spring_EPAM.entity.Trainee;
import com.example.task_Spring_EPAM.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDAOTest {

    private TraineeDAO traineeDAO;
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
        traineeDAO = new TraineeDAO();
        traineeDAO.setStorage(storage);
    }

    @Test
    void testSaveAndFindByUsername() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        traineeDAO.save(trainee);
        Optional<Trainee> found = traineeDAO.findByUsername("john.doe");

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<Trainee> found = traineeDAO.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByUsername() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");

        traineeDAO.save(trainee);
        traineeDAO.deleteByUsername("john.doe");
        Optional<Trainee> found = traineeDAO.findByUsername("john.doe");

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("John");

        traineeDAO.save(trainee);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setUsername("john.doe");
        updatedTrainee.setFirstName("Johnny");

        traineeDAO.updateTrainee(updatedTrainee);

        Optional<Trainee> found = traineeDAO.findByUsername("john.doe");

        assertTrue(found.isPresent());
        assertEquals("Johnny", found.get().getFirstName());
    }

    @Test
    void testGetAllTrainees() {
        Trainee trainee1 = new Trainee();
        trainee1.setUsername("john.doe");
        Trainee trainee2 = new Trainee();
        trainee2.setUsername("jane.doe");

        traineeDAO.save(trainee1);
        traineeDAO.save(trainee2);

        Collection<Trainee> trainees = traineeDAO.getAllTrainees();

        assertEquals(2, trainees.size());
        assertTrue(trainees.contains(trainee1));
        assertTrue(trainees.contains(trainee2));
    }
}
