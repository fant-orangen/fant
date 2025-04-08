-- The BCryptPasswordEncoder has encoded the password 'password123'
INSERT INTO users (email, display_name, password_hash)
VALUES ('alice@example.com', 'alice', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash, role)
VALUES ('jakob@mail.com', 'jakob', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW', 'ADMIN');

INSERT INTO users (email, display_name, password_hash)
VALUES ('ola@norge.no', 'ola', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO categories (name, image_url)
VALUES ('Travel', 'travel');

INSERT INTO categories (name, image_url)
VALUES ('Appliance', 'appliance');

INSERT INTO categories (name, image_url)
VALUES ('Boat', 'boat');

INSERT INTO categories (name, image_url)
VALUES ('Book', 'book');

INSERT INTO categories (name, image_url)
VALUES ('Camera', 'camera');

INSERT INTO categories (name, image_url)
VALUES ('Car', 'car');

INSERT INTO categories (name, image_url)
VALUES ('Clothes', 'clothes');

INSERT INTO categories (name, image_url)
VALUES ('Computer', 'computer');

INSERT INTO categories (name, image_url)
VALUES ('Furniture', 'furniture');

INSERT INTO categories (name, image_url)
VALUES ('Motorcycle', 'motorcycle');

INSERT INTO categories (name, image_url)
VALUES ('Phone', 'phone');

INSERT INTO categories (name, image_url)
VALUES ('Art', 'art');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 1, 'Test', 'Test', 500.00, 'ACTIVE', NOW(), NOW(),62,5.7);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (1, 1, 'Lambi Deluxe', 'Pent brukt', 200.00, 'ACTIVE', NOW(), NOW(), 62, 8.1);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (1, 1, 'Mills Majones', 'Test', 500.00, 'ACTIVE', NOW(), NOW(), 59, 7.5);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (1, 1, 'Ny bil!!!!', 'Test', 100.00, 'ACTIVE', NOW(), NOW(), 31, 98);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (1, 1, 'Vaskepulver med jordbærsmak', 'Et fantastisk vaskepulver med smak av jordbær! Mmm!', 240.00, 'ACTIVE',
        NOW(), NOW(), 60, 6.5);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (2, 1, 'Dinosaurkjæledyr', 'Han heter Ronald.', 240.00, 'ACTIVE', NOW(), NOW(), 60, 5.5);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (2, 1, 'Femten planker', 'Femten planker jeg ikke trenger. Høy sentimental verdi!', 15000.00, 'ACTIVE', NOW(),
        NOW(), 59, 5.5);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (2, 2, 'Bringebærsyltetøy', 'Ikke så mye mer å si', 59.0, 'ACTIVE', NOW(), NOW(), 62, 7.5);

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (2, 2, 'Diskotek', 'Selger gammelt diskotek; familiebusiness.', 9000000.00, 'ACTIVE', NOW(), NOW(), 62.4, 6.15);

-- Images for items

-- Insert mock images for item 2 (Lambi Deluxe)
INSERT INTO item_images (item_id, image_url, position)
VALUES (2, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050388/lambi_l19hef.jpg', 0);

-- Insert mock images for item 3 (Mills Majones)
INSERT INTO item_images (item_id, image_url, position)
VALUES (3, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/mills_lkbae2.jpg', 0);

-- Insert mock images for item 4 (Ny bil!!!!)
INSERT INTO item_images (item_id, image_url, position)
VALUES (4, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/lambo_tjlhrf.jpg', 0);

-- Insert mock images for item 5 (Vaskepulver med jordbærsmak)
INSERT INTO item_images (item_id, image_url, position)
VALUES (5, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/vaskepulver_djpggn.webp', 0);

-- Insert mock images for item 6 (Dinosaurkjæledyr)
INSERT INTO item_images (item_id, image_url, position)
VALUES (6, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/velociraptor_njtom5.webp', 0);

-- Insert mock images for item 7 (Femten planker)
INSERT INTO item_images (item_id, image_url, position)
VALUES (7, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/15_planks_plych1.jpg', 0);

-- Insert mock images for item 8 (Bringebærsyltetøy)
INSERT INTO item_images (item_id, image_url, position)
VALUES (8, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/syltetoy_vtpn8w.png', 0);

-- Insert mock images for item 9 (Diskotek)
INSERT INTO item_images (item_id, image_url, position)
VALUES (9, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744050389/discoteque_oxmg4h.jpg', 0);

-- Inserts for mock conversation history
-- Example 1: Using DATEADD function for H2 compatibility
INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (1, 2, 3, 'Hi, is this Mills Majones still available?', DATEADD('HOUR', -2, CURRENT_TIMESTAMP()), true);

-- Example 2: Using a specific timestamp
INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (2, 1, 3, 'Yes, it is! Are you interested in buying it?', DATEADD('HOUR', -1, CURRENT_TIMESTAMP()), true);

-- Example 3: Using current timestamp
INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (1, 2, 3, 'I am! How fresh is it?', CURRENT_TIMESTAMP(), false);

-- Message pagination test

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Hi!', DATEADD('HOUR', -18, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (1, 3, 5, 'Hi?', DATEADD('HOUR', -17, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'I want you alice', DATEADD('HOUR', -16, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (1, 3, 5, 'Im taken', DATEADD('HOUR', -15, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'No. Im coming to your house tonight', DATEADD('HOUR', -14, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'You should look forward to it', DATEADD('HOUR', -13, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (1, 3, 5, 'Im going to call the police', DATEADD('HOUR', -12, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Do it', DATEADD('HOUR', -11, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Theyll never believe you', DATEADD('HOUR', -10, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'I think about you alice', DATEADD('HOUR', -9, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Every day', DATEADD('HOUR', -8, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Are you there?', DATEADD('HOUR', -7, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Dont ignore me alice', DATEADD('HOUR', -6, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'I know youre reading this', DATEADD('HOUR', -5, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Answer me!', DATEADD('HOUR', -4, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Alice please', DATEADD('HOUR', -3, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Ill die without you', DATEADD('HOUR', -2, CURRENT_TIMESTAMP()), false);

INSERT INTO messages (sender_id, receiver_id, item_id, content, sent_at, `read`)
VALUES (3, 1, 5, 'Alice', DATEADD('HOUR', -1, CURRENT_TIMESTAMP()), false);

-- Place test bids
-- Multiple bids on item 1
INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (1, 2, 95.00, 'I can pick it up today', 'PENDING');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (1, 3, 105.50, 'Very interested in this item', 'ACCEPTED');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (3, 2, 260.00, 'I can pick it up this weekend', 'PENDING');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (2, 3, 45.00, 'Is this still available?', 'PENDING');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (4, 1, 19.50, 'Would you take $19.50?', 'ACCEPTED');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (5, 1, 75.00, 'Is the condition still good?', 'PENDING');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (6, 2, 320.00, 'Can you provide more photos?', 'PENDING');

INSERT INTO bids (item_id, bidder_id, amount, comment, status)
VALUES (7, 2, 15.25, 'Is this the latest version?', 'PENDING');
