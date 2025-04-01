-- The BCryptPasswordEncoder has encoded the password 'password123'
INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
VALUES (1, 'alice', 'alice@example.com', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW', 'USER', NOW(), NOW());

INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
VALUES (2, 'jakob', 'jakob@mail.com', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW', 'ADMIN', NOW(), NOW());