-- Insert training types
INSERT INTO training_types (training_type_name) VALUES
                                                    ('Yoga'), ('Fitness'), ('Pilates');

-- Insert users
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES
                                                                             ('John', 'Doe', 'john.doe', '$2a$10$7QKTUOmB27UaxZW3cibT3OG9FbDdsIIfHb7IxbdSNmzVp16N9UhLq', true), -- Hashed password
                                                                             ('Jane', 'Doe', 'jane.doe', '$2a$10$N.WFPL5NppduOYCsVkqUn.SdMnONdddzqU59NzpL6ub1fDGGvDIW.', true); -- Hashed password

-- Insert trainers
INSERT INTO trainers (specialization_id, user_id) VALUES
    ((SELECT id FROM training_types WHERE training_type_name = 'Yoga'), (SELECT id FROM users WHERE username = 'jane.doe'));

-- Insert trainees
INSERT INTO trainees (date_of_birth, address, user_id) VALUES
    ('1990-01-15', '123 Main St', (SELECT id FROM users WHERE username = 'john.doe'));

-- Insert training sessions
INSERT INTO training (trainee_id, trainer_id, training_type_id, training_name, training_date, training_duration) VALUES
    ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'john.doe')),
     (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'jane.doe')),
     (SELECT id FROM training_types WHERE training_type_name = 'Yoga'),
     'Yoga Session', '2025-02-26', 60);

-- Map trainee to trainer
INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES
    ((SELECT id FROM trainees WHERE user_id = (SELECT id FROM users WHERE username = 'john.doe')),
     (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'jane.doe')));
