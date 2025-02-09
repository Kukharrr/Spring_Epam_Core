package com.example.task_Spring_EPAM.service;

import com.example.task_Spring_EPAM.dao.TraineeDAO;
import com.example.task_Spring_EPAM.entity.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainee_NewUser() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDAO.findByUsername("john.doe")).thenReturn(Optional.empty());

        traineeService.createTrainee(trainee);

        assertEquals("john.doe", trainee.getUsername());
        assertNotNull(trainee.getPassword());
        assertTrue(trainee.isActive());

        verify(traineeDAO).save(trainee);
    }

    @Test
    void testCreateTrainee_DuplicateUser() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDAO.findByUsername("john.doe")).thenReturn(Optional.of(new Trainee()));
        when(traineeDAO.findByUsername("john.doe1")).thenReturn(Optional.empty());

        traineeService.createTrainee(trainee);

        assertEquals("john.doe1", trainee.getUsername());
        assertNotNull(trainee.getPassword());
        assertTrue(trainee.isActive());

        verify(traineeDAO).save(trainee);
    }

    @Test
    void testFindTraineeByUsername_Found() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");

        when(traineeDAO.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        Optional<Trainee> found = traineeService.findTraineeByUsername("john.doe");

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void testFindTraineeByUsername_NotFound() {
        when(traineeDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<Trainee> found = traineeService.findTraineeByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteTrainee() {
        traineeService.deleteTrainee("john.doe");

        verify(traineeDAO).deleteByUsername("john.doe");
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");

        traineeService.updateTrainee(trainee);

        verify(traineeDAO).updateTrainee(trainee);
    }

    @Test
    void testGetAllTrainees() {
        traineeService.getAllTrainees();

        verify(traineeDAO).getAllTrainees();
    }
}
