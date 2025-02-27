package com.example.epam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "training_types")
@Getter
@Setter
@NoArgsConstructor
public class TrainingType {
    private static final Logger logger = LoggerFactory.getLogger(TrainingType.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type_name")
    private String trainingTypeName;
}