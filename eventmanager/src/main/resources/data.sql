-- Роли
INSERT IGNORE INTO roles (id, name) VALUES (1, 'USER');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ORGANIZER');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ADMIN');

-- Админ-пользователь
INSERT IGNORE INTO users (id, username, email, password)
VALUES (1, 'admin', 'admin@example.com', '$2a$10$nOUIs5kJ7naTuTFkBy1veuEvaf3eQj1rbeG/bkKIiYDpG8z7RLbAG');
-- пароль: admin123

-- Связь: admin — ADMIN
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 3);



