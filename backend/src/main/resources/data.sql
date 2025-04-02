-- The BCryptPasswordEncoder has encoded the password 'password123'
INSERT INTO users (email, display_name, password_hash)
VALUES ('alice@example.com', 'alice', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash, role)
VALUES ('jakob@mail.com', 'jakob', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW', 'ADMIN');

INSERT INTO categories (name, description)
VALUES ('Test', 'Test');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (1, 1, 'Test', 'Test', 500.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (1, 1, 'Test', 'Test', 200.00, 'ACTIVE', NOW(), NOW());