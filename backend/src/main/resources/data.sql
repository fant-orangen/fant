-- Testing
INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
VALUES (1, 'alice', 'alice@example.com', 'hashed', 'USER', NOW(), NOW());
