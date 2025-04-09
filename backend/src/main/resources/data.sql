-- The BCryptPasswordEncoder has encoded the password 'password123'
INSERT INTO users (email, display_name, password_hash)
VALUES ('alice@example.com', 'alice', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash, role)
VALUES ('jakob@mail.com', 'jakob', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW', 'ADMIN');

INSERT INTO users (email, display_name, password_hash)
VALUES ('ola@norge.no', 'ola', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('mariah@example.com', 'mariah', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('kevinh@example.com', 'kevinh', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('sofia@example.com', 'sofia', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('julian@example.com', 'julian', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('oliverb@example.com', 'oliverb', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('emilys@example.com', 'emilys', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('danielm@example.com', 'danielm', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('lucia@example.com', 'lucia', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('noahk@example.com', 'noahk', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

INSERT INTO users (email, display_name, password_hash)
VALUES ('ameliaw@example.com', 'ameliaw', '$2a$10$9JB0yUlymHFA23jzoN9VWOPF6UIjLPCWFxwYV02kI/MlYdNJsDlBW');

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

-- joke images

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

-- New Items
INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 6, 'iPhone 13 Pro', 'Recently serviced and updated.', 875.41, 'ACTIVE', NOW(), NOW(), 59.8762, 6.1148);
INSERT INTO images (item_id, url)
VALUES (1, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (7, 8, 'Mountain Bike', 'Minimal signs of use, clean and well-maintained.', 1015.33, 'ACTIVE', NOW(), NOW(), 60.1170, 8.7559);
INSERT INTO images (item_id, url)
VALUES (2, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 10, 'Gaming Laptop', 'Fully functional with no defects.', 1621.29, 'ACTIVE', NOW(), NOW(), 61.7899, 6.2533);
INSERT INTO images (item_id, url)
VALUES (3, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (8, 10, 'Smart TV 55"', 'In excellent condition, barely used.', 1477.15, 'ACTIVE', NOW(), NOW(), 60.3226, 7.1488);
INSERT INTO images (item_id, url)
VALUES (4, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 3, 'AirPods Pro', 'Selling due to upgrade, works perfectly.', 165.98, 'ACTIVE', NOW(), NOW(), 59.6511, 6.9327);
INSERT INTO images (item_id, url)
VALUES (5, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (10, 7, 'Dining Table Set', 'Comes with all original packaging and accessories.', 433.69, 'ACTIVE', NOW(), NOW(), 59.2727, 10.1865);
INSERT INTO images (item_id, url)
VALUES (6, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (7, 9, 'Leather Office Chair', 'Gently used, still under warranty.', 124.93, 'ACTIVE', NOW(), NOW(), 60.6637, 5.7882);
INSERT INTO images (item_id, url)
VALUES (7, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 4, 'Bluetooth Speaker', 'Recently serviced and updated.', 199.99, 'ACTIVE', NOW(), NOW(), 61.1212, 5.9974);
INSERT INTO images (item_id, url)
VALUES (8, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (4, 2, '4K Action Camera', 'Perfect for outdoor activities and travel.', 229.00, 'ACTIVE', NOW(), NOW(), 60.5031, 6.3217);
INSERT INTO images (item_id, url)
VALUES (9, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 3, 'Robot Vacuum', 'Great for both home and office use.', 349.90, 'ACTIVE', NOW(), NOW(), 58.9344, 9.6522);
INSERT INTO images (item_id, url)
VALUES (10, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 8, 'Espresso Machine', 'In excellent condition, barely used.', 499.00, 'ACTIVE', NOW(), NOW(), 59.4023, 10.3271);
INSERT INTO images (item_id, url)
VALUES (11, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (8, 4, 'Winter Jacket', 'Minimal signs of use, clean and well-maintained.', 149.50, 'ACTIVE', NOW(), NOW(), 60.7234, 5.8321);
INSERT INTO images (item_id, url)
VALUES (12, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 1, 'Electric Scooter', 'Selling due to upgrade, works perfectly.', 699.99, 'ACTIVE', NOW(), NOW(), 58.7091, 9.1512);
INSERT INTO images (item_id, url)
VALUES (13, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (4, 5, 'Camping Tent', 'Comes with all original packaging and accessories.', 249.95, 'ACTIVE', NOW(), NOW(), 60.9412, 7.2312);
INSERT INTO images (item_id, url)
VALUES (14, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (9, 9, 'Desk Lamp', 'Gently used, still under warranty.', 45.00, 'ACTIVE', NOW(), NOW(), 62.0021, 6.4923);
INSERT INTO images (item_id, url)
VALUES (15, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 2, 'Washing Machine', 'Fully functional with no defects.', 1200.00, 'ACTIVE', NOW(), NOW(), 59.8123, 5.9911);
INSERT INTO images (item_id, url)
VALUES (16, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 7, 'Bookshelf', 'Recently serviced and updated.', 320.75, 'ACTIVE', NOW(), NOW(), 61.0012, 6.0044);
INSERT INTO images (item_id, url)
VALUES (17, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (10, 6, 'Smartwatch', 'Perfect for outdoor activities and travel.', 269.00, 'ACTIVE', NOW(), NOW(), 60.1123, 9.3011);
INSERT INTO images (item_id, url)
VALUES (18, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 3, 'Tablet 10.5"', 'Selling due to upgrade, works perfectly.', 499.00, 'ACTIVE', NOW(), NOW(), 59.1098, 10.1000);
INSERT INTO images (item_id, url)
VALUES (19, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 2, 'Graphic Drawing Tablet', 'Great for both home and office use.', 389.99, 'ACTIVE', NOW(), NOW(), 61.2201, 6.2011);
INSERT INTO images (item_id, url)
VALUES (20, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (8, 10, 'Home Theater System', 'In excellent condition, barely used.', 999.99, 'ACTIVE', NOW(), NOW(), 60.1323, 8.1234);
INSERT INTO images (item_id, url)
VALUES (21, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (9, 5, 'Fridge Freezer Combo', 'Recently serviced and updated.', 1299.00, 'ACTIVE', NOW(), NOW(), 59.4211, 6.8765);
INSERT INTO images (item_id, url)
VALUES (22, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 8, 'Garden Tools Set', 'Perfect for outdoor activities and travel.', 179.00, 'ACTIVE', NOW(), NOW(), 58.9356, 10.4412);
INSERT INTO images (item_id, url)
VALUES (23, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 6, 'Pressure Washer', 'Selling due to upgrade, works perfectly.', 249.00, 'ACTIVE', NOW(), NOW(), 60.7452, 7.3021);
INSERT INTO images (item_id, url)
VALUES (24, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (4, 4, 'Microwave Oven', 'Minimal signs of use, clean and well-maintained.', 139.90, 'ACTIVE', NOW(), NOW(), 59.1234, 5.8765);
INSERT INTO images (item_id, url)
VALUES (25, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (10, 3, 'Pet Carrier', 'Comes with all original packaging and accessories.', 59.90, 'ACTIVE', NOW(), NOW(), 58.9123, 10.2012);
INSERT INTO images (item_id, url)
VALUES (26, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 1, 'Baby Stroller', 'Gently used, still under warranty.', 450.00, 'ACTIVE', NOW(), NOW(), 60.8345, 6.4411);
INSERT INTO images (item_id, url)
VALUES (27, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 2, 'Portable AC Unit', 'Fully functional with no defects.', 549.00, 'ACTIVE', NOW(), NOW(), 59.7012, 8.1100);
INSERT INTO images (item_id, url)
VALUES (28, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 3, 'Fitness Tracker', 'Recently serviced and updated.', 89.90, 'ACTIVE', NOW(), NOW(), 58.9731, 9.9999);
INSERT INTO images (item_id, url)
VALUES (29, 'link');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (7, 10, 'Digital Piano', 'Great for both home and office use.', 799.00, 'ACTIVE', NOW(), NOW(), 60.1000, 6.6543);
INSERT INTO images (item_id, url)
VALUES (30, 'link');

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
