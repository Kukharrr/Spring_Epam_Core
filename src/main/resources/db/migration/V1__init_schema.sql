CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_active BOOLEAN NOT NULL
);

CREATE TABLE trainees (
                          id SERIAL PRIMARY KEY,
                          date_of_birth DATE,
                          address VARCHAR(255),
                          user_id INT NOT NULL,
                          CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE trainers (
                          id SERIAL PRIMARY KEY,
                          specialization VARCHAR(255),
                          user_id INT NOT NULL,
                          CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE training_types (
                                id SERIAL PRIMARY KEY,
                                training_type_name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE training (
                          id SERIAL PRIMARY KEY,
                          trainee_id INT NOT NULL,
                          trainer_id INT NOT NULL,
                          training_type_id INT NOT NULL,
                          training_name VARCHAR(255) NOT NULL,
                          training_date DATE NOT NULL,
                          training_duration INT NOT NULL,
                          CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE,
                          CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE,
                          CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_types(id) ON DELETE CASCADE
);
