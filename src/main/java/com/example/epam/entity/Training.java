package com.example.epam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Entity
@Table(name = "training")
@Getter
@Setter
@NoArgsConstructor
public class Training {
    private static final Logger logger = LoggerFactory.getLogger(Training.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_name")
    private String trainingName;

    @Column(name = "training_date")
    private LocalDate trainingDate;

    @Column(name = "training_duration")
    private Integer trainingDuration;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_training_trainee"))
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_training_trainer"))
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_training_type"))
    private TrainingType trainingType;
}