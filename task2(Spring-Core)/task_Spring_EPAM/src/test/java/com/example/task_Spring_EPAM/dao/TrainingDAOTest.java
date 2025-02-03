package com.example.task_Spring_EPAM.dao;

import com.example.task_Spring_EPAM.entity.Training;
import com.example.task_Spring_EPAM.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDAOTest {

    private TrainingDAO trainingDAO;
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
        trainingDAO = new TrainingDAO();
        trainingDAO.setStorage(storage);
    }

    @Test
    void testSaveAndFindById() {
        Training training = new Training();
        training.setId(1);
        training.setTrainingName("Yoga Session");

        trainingDAO.save(training);
        Optional<Training> found = trainingDAO.findById(1);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getId());
        assertEquals("Yoga Session", found.get().getTrainingName());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Training> found = trainingDAO.findById(99);

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteById() {
        Training training = new Training();
        training.setId(1);

        trainingDAO.save(training);
        trainingDAO.deleteById(1);
        Optional<Training> found = trainingDAO.findById(1);

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateTraining() {
        Training training = new Training();
        training.setId(1);
        training.setTrainingName("Morning Yoga");

        trainingDAO.save(training);

        Training updatedTraining = new Training();
        updatedTraining.setId(1);
        updatedTraining.setTrainingName("Evening Yoga");

        trainingDAO.updateTraining(updatedTraining);

        Optional<Training> found = trainingDAO.findById(1);

        assertTrue(found.isPresent());
        assertEquals("Evening Yoga", found.get().getTrainingName());
    }

    @Test
    void testGetAllTrainings() {
        Training training1 = new Training();
        training1.setId(1);
        Training training2 = new Training();
        training2.setId(2);

        trainingDAO.save(training1);
        trainingDAO.save(training2);

        Collection<Training> trainings = trainingDAO.getAllTrainings();

        assertEquals(2, trainings.size());
        assertTrue(trainings.contains(training1));
        assertTrue(trainings.contains(training2));
    }
}
