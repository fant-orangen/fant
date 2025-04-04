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

-- Images for items

-- Insert mock images for item 2 (Lambi Deluxe)
INSERT INTO item_images (item_id, image_url, position)
VALUES (2, 'https://i.ibb.co/wrBBYXmw/lambi.jpg', 0);

-- Insert mock images for item 3 (Mills Majones)
INSERT INTO item_images (item_id, image_url, position)
VALUES (3, 'https://i.ibb.co/VYxptvrF/mills.jpg', 0);

-- Insert mock images for item 4 (Ny bil!!!!)
INSERT INTO item_images (item_id, image_url, position)
VALUES (4, 'https://i.ibb.co/FkRNqvG3/lambo.jpg', 0);

-- Insert mock images for item 5 (Vaskepulver med jordbærsmak)
INSERT INTO item_images (item_id, image_url, position)
VALUES (5, 'https://i.ibb.co/HfHbnGNs/vaskepulver.webp', 0);

-- Insert mock images for item 6 (Dinosaurkjæledyr)
INSERT INTO item_images (item_id, image_url, position)
VALUES (6, 'https://i.ibb.co/h1922gsJ/velociraptor.webp', 0);

-- Insert mock images for item 7 (Femten planker)
INSERT INTO item_images (item_id, image_url, position)
VALUES (7, 'https://i.ibb.co/Y5mZ4DJ/15-planks.jpg', 0);


-- Insert mock images for item 8 (Bringebærsyltetøy)
INSERT INTO item_images (item_id, image_url, position)
VALUES (8, 'https://i.ibb.co/ktPs8NS/syltetoy.png', 0);

-- Insert mock images for item 9 (Diskotek)
INSERT INTO item_images (item_id, image_url, position)
VALUES (9, 'https://i.ibb.co/HTFJ6ky9/discoteque.jpg', 0);

-- Inserts for mock conversation history
-- First message: Initial inquiry
INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, read)
VALUES (1, 2, 3, 'Hi, is this Mills Majones still available?', CURRENT_TIMESTAMP - INTERVAL 2 HOUR, true);

-- Second message: Response from seller
INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, read)
VALUES (2, 1, 3, 'Yes, it is still available. Are you interested in buying it?', CURRENT_TIMESTAMP - INTERVAL 1 HOUR, true);

-- Third message: Follow-up question
INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, read)
VALUES (1, 2, 3, 'Great! What\'s the expiration date on it?', CURRENT_TIMESTAMP - INTERVAL 30 MINUTE, false);

