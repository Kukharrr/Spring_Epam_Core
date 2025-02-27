CREATE TABLE training_types (
                                id BIGSERIAL PRIMARY KEY,
                                training_type_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE trainers (
                          id BIGSERIAL PRIMARY KEY,
                          specialization_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          FOREIGN KEY (specialization_id) REFERENCES training_types(id),
                          FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE trainees (
                          id BIGSERIAL PRIMARY KEY,
                          date_of_birth DATE,
                          address VARCHAR(255),
                          user_id BIGINT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE training (
                          id BIGSERIAL PRIMARY KEY,
                          trainee_id BIGINT NOT NULL,
                          trainer_id BIGINT NOT NULL,
                          training_type_id BIGINT NOT NULL,
                          training_name VARCHAR(255) NOT NULL,
                          training_date DATE NOT NULL,
                          training_duration INTEGER NOT NULL,
                          FOREIGN KEY (trainee_id) REFERENCES trainees(id),
                          FOREIGN KEY (trainer_id) REFERENCES trainers(id),
                          FOREIGN KEY (training_type_id) REFERENCES training_types(id)
);

CREATE TABLE trainee_trainer (
                                 trainee_id BIGINT NOT NULL,
                                 trainer_id BIGINT NOT NULL,
                                 FOREIGN KEY (trainee_id) REFERENCES trainees(id),
                                 FOREIGN KEY (trainer_id) REFERENCES trainers(id),
                                 PRIMARY KEY (trainee_id, trainer_id)
);