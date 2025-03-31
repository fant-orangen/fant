-- The BCryptPasswordEncoder has encoded the password 'password123'
INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
VALUES (1, 'alice', 'alice@example.com', '$2a$10$3ZP90w4a0j7aDReadTZ0Qeu4QVjA5q/TMwSDWwqVzDfKbGZEMtGGi', 'USER', NOW(), NOW());