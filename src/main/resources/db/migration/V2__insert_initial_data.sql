INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES
    ('John', 'Doe', 'johndoe', 'password123', true),
    ('Jane', 'Smith', 'janesmith', 'securepass', true);

INSERT INTO trainees (date_of_birth, address, user_id)
VALUES
    ('1990-05-15', '123 Main St', 1),
    ('1985-08-20', '456 Elm St', 2);

INSERT INTO trainers (specialization, user_id)
VALUES
    ('Fitness', 1),
    ('Yoga', 2);

INSERT INTO training_types (training_type_name)
VALUES
    ('Strength Training'),
    ('Cardio'),
    ('Yoga');

INSERT INTO training (trainee_id, trainer_id, training_type_id, training_name, training_date, training_duration)
VALUES
    (1, 1, 1, 'Weight Lifting', '2025-03-01', 60),
    (2, 2, 3, 'Yoga Basics', '2025-03-05', 45);
