-- Insert roles
INSERT INTO roles (name)
VALUES ('ROLE_USER');
INSERT INTO roles (name)
VALUES ('ROLE_ADMIN');

-- Insert users
INSERT INTO users (username, email, password)
VALUES ('admin', 'admin@email.com', '$2a$10$kJH7WRrC0oY0xxUBckd2O.GDyknKigeGoMaRdnkgqEgqOPboHYOSK'),
       ('user', 'user@email.com', '$2a$10$8JEUuObNyYunLl.O6dRYV.NIR5HH3GE0OcZhnIihIPu3I4ECOicMu');

-- Assign roles
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')),
       ((SELECT id FROM users WHERE username = 'user'), (SELECT id FROM roles WHERE name = 'ROLE_USER'));
