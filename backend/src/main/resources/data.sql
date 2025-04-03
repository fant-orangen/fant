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
VALUES (1, 1, 'Lambi Deluxe', 'Test', 200.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (1, 1, 'Mills Majones', 'Test', 500.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (1, 1, 'Ny bil!!!!', 'Test', 100.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (1, 1, 'Vaskepulver med jordbærsmak', 'Et fantastisk vaskepulver med smak av jordbær! Mmm!', 240.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (2, 1, 'Dinosaurkjæledyr', 'Han heter Ronald.', 240.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (2, 1, 'Femten planker', 'Femten planker jeg ikke trenger. Høy sentimental verdi!', 15000.00, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (2, 1, 'Bringebærsyltetøy', 'Ikke så mye mer å si', 59.0, 'ACTIVE', NOW(), NOW());

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at)
VALUES (2, 1, 'Diskotek', 'Selger gammelt diskotek; familiebusiness.', 9000000.00, 'ACTIVE', NOW(), NOW());