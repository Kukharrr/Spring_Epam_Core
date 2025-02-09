package com.example.task_Spring_EPAM.service;

import com.example.task_Spring_EPAM.dao.TrainerDAO;
import com.example.task_Spring_EPAM.entity.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainer_NewUser() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");

        when(trainerDAO.findByUsername("alice.smith")).thenReturn(Optional.empty());

        trainerService.createTrainer(trainer);

        assertEquals("alice.smith", trainer.getUsername());
        assertNotNull(trainer.getPassword());
        assertTrue(trainer.isActive());

        verify(trainerDAO).save(trainer);
    }

    @Test
    void testCreateTrainer_DuplicateUser() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");

        when(trainerDAO.findByUsername("alice.smith")).thenReturn(Optional.of(new Trainer()));
        when(trainerDAO.findByUsername("alice.smith1")).thenReturn(Optional.empty());

        trainerService.createTrainer(trainer);

        assertEquals("alice.smith1", trainer.getUsername());
        assertNotNull(trainer.getPassword());
        assertTrue(trainer.isActive());

        verify(trainerDAO).save(trainer);
    }

    @Test
    void testFindTrainerByUsername_Found() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alice.smith");

        when(trainerDAO.findByUsername("alice.smith")).thenReturn(Optional.of(trainer));

        Optional<Trainer> found = trainerService.findTrainerByUsername("alice.smith");

        assertTrue(found.isPresent());
        assertEquals("alice.smith", found.get().getUsername());
    }

    @Test
    void testFindTrainerByUsername_NotFound() {
        when(trainerDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<Trainer> found = trainerService.findTrainerByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteTrainer() {
        trainerService.deleteTrainer("alice.smith");

        verify(trainerDAO).deleteByUsername("alice.smith");
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alice.smith");

        trainerService.updateTrainer(trainer);

        verify(trainerDAO).updateTrainer(trainer);
    }

    @Test
    void testGetAllTrainers() {
        trainerService.getAllTrainers();

        verify(trainerDAO).getAllTrainers();
    }
}
