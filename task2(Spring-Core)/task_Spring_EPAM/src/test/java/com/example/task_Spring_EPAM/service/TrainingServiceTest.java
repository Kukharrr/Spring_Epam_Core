package com.example.task_Spring_EPAM.service;

import com.example.task_Spring_EPAM.dao.TrainingDAO;
import com.example.task_Spring_EPAM.entity.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();

        when(trainingDAO.getAllTrainings()).thenReturn(Collections.emptyList());

        trainingService.createTraining(training);

        assertEquals(1, training.getId());

        verify(trainingDAO).save(training);
    }

    @Test
    void testFindTrainingById_Found() {
        Training training = new Training();
        training.setId(1);

        when(trainingDAO.findById(1)).thenReturn(Optional.of(training));

        Optional<Training> found = trainingService.findTrainingById(1);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getId());
    }

    @Test
    void testFindTrainingById_NotFound() {
        when(trainingDAO.findById(99)).thenReturn(Optional.empty());

        Optional<Training> found = trainingService.findTrainingById(99);

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteTraining() {
        trainingService.deleteTraining(1);

        verify(trainingDAO).deleteById(1);
    }

    @Test
    void testUpdateTraining() {
        Training training = new Training();
        training.setId(1);

        trainingService.updateTraining(training);

        verify(trainingDAO).updateTraining(training);
    }

    @Test
    void testGetAllTrainings() {
        trainingService.getAllTrainings();

        verify(trainingDAO).getAllTrainings();
    }
}
