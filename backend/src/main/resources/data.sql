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
VALUES (6, 1, 'Travel Backpack', 'Selling due to upgrade, works perfectly.', 689.66, 'ACTIVE', NOW(), NOW(), 58.7622, 8.0288);
INSERT INTO item_images (item_id, image_url)
VALUES (10, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217389/backpack_lzwcps.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 1, 'Hiking Boots', 'Selling due to upgrade, works perfectly.', 1071.50, 'ACTIVE', NOW(), NOW(), 62.3815, 6.0692);
INSERT INTO item_images (item_id, image_url)
VALUES (11, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/hikingboots_ejkkus.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 1, 'Suitcase Set', 'Recently serviced and updated.', 1148.48, 'ACTIVE', NOW(), NOW(), 60.3145, 5.8573);
INSERT INTO item_images (item_id, image_url)
VALUES (12, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/suitcase_set_ce8ui4.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 2, 'Blender', 'Clean and well-maintained.', 1852.83, 'ACTIVE', NOW(), NOW(), 60.8479, 10.0623);
INSERT INTO item_images (item_id, image_url)
VALUES (13, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/blender_cca71d.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 2, 'Microwave Oven', 'Clean and well-maintained.', 1846.57, 'ACTIVE', NOW(), NOW(), 60.1133, 9.5391);
INSERT INTO item_images (item_id, image_url)
VALUES (14, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/microwave_i4o9ty.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (7, 2, 'Dishwasher', 'Fully functional with no defects.', 176.36, 'ACTIVE', NOW(), NOW(), 60.7759, 10.3393);
INSERT INTO item_images (item_id, image_url)
VALUES (15, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/dishwasher_g7osez.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (9, 3, 'Fishing Boat', 'Comes with original packaging.', 1232.21, 'ACTIVE', NOW(), NOW(), 58.9671, 5.3895);
INSERT INTO item_images (item_id, image_url)
VALUES (16, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/fishing_boat_axttfi.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (9, 3, 'Inflatable Kayak', 'Comes with original packaging.', 1719.19, 'ACTIVE', NOW(), NOW(), 58.1406, 9.4779);
INSERT INTO item_images (item_id, image_url)
VALUES (17, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/inflatable_kayak_jrcngn.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 4, 'Fantasy Novel Set', 'Clean and well-maintained.', 1386.38, 'ACTIVE', NOW(), NOW(), 59.4078, 10.3789);
INSERT INTO item_images (item_id, image_url)
VALUES (18, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/fantasynovels_ygootf.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 4, 'Programming Book', 'Great for both home and travel use.', 1125.07, 'ACTIVE', NOW(), NOW(), 60.8354, 8.7327);
INSERT INTO item_images (item_id, image_url)
VALUES (19, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/programmingbook_zsm3ur.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 5, 'DSLR Camera', 'Selling due to upgrade, works perfectly.', 1175.18, 'ACTIVE', NOW(), NOW(), 60.9231, 6.1481);
INSERT INTO item_images (item_id, image_url)
VALUES (20, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/dslr_vpnsff.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 5, 'Tripod Stand', 'Recently serviced and updated.', 196.72, 'ACTIVE', NOW(), NOW(), 60.8781, 7.9468);
INSERT INTO item_images (item_id, image_url)
VALUES (21, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/tripod_stand_bgtupf.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 6, 'Used Sedan', 'Fully functional with no defects.', 1686.30, 'ACTIVE', NOW(), NOW(), 62.1861, 10.7366);
INSERT INTO item_images (item_id, image_url)
VALUES (22, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sedan_w4x7ii.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (8, 6, 'Electric Car Charger', 'In excellent condition, barely used.', 1226.99, 'ACTIVE', NOW(), NOW(), 61.1925, 6.1988);
INSERT INTO item_images (item_id, image_url)
VALUES (23, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/carcharger_j3ka4b.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 6, 'Car Roof Box', 'Clean and well-maintained.', 413.66, 'ACTIVE', NOW(), NOW(), 58.8753, 6.0057);
INSERT INTO item_images (item_id, image_url)
VALUES (24, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/car_roof_box_dcrmcp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 7, 'Leather Jacket', 'Selling due to upgrade, works perfectly.', 1006.28, 'ACTIVE', NOW(), NOW(), 59.6358, 6.2118);
INSERT INTO item_images (item_id, image_url)
VALUES (25, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/leatherjacket_aakts3.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (6, 7, 'Summer Dress', 'Great for both home and travel use.', 350.73, 'ACTIVE', NOW(), NOW(), 58.2245, 8.8889);
INSERT INTO item_images (item_id, image_url)
VALUES (26, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/summerdress_ijyeg2.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (4, 8, 'Gaming Laptop', 'Comes with original packaging.', 1047.58, 'ACTIVE', NOW(), NOW(), 60.1422, 9.3871);
INSERT INTO item_images (item_id, image_url)
VALUES (27, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/gaminglaptop_ihp4gq.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 8, 'Desktop PC', 'Recently serviced and updated.', 1463.84, 'ACTIVE', NOW(), NOW(), 61.7449, 10.5132);
INSERT INTO item_images (item_id, image_url)
VALUES (28, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/desktoppc_xggmrk.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (7, 8, 'Computer Monitor', 'Selling due to upgrade, works perfectly.', 1703.86, 'ACTIVE', NOW(), NOW(), 60.2293, 8.7742);
INSERT INTO item_images (item_id, image_url)
VALUES (29, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/monitor_tc8ibs.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (4, 9, 'Dining Table', 'Great for both home and travel use.', 1732.58, 'ACTIVE', NOW(), NOW(), 58.2995, 5.5864);
INSERT INTO item_images (item_id, image_url)
VALUES (30, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/diningtable_rh9ane.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (3, 9, 'Bookshelf', 'Great for both home and travel use.', 1954.49, 'ACTIVE', NOW(), NOW(), 61.6511, 10.0269);
INSERT INTO item_images (item_id, image_url)
VALUES (31, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/bookshelf_z9fsqt.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (5, 9, 'Recliner Chair', 'Clean and well-maintained.', 1684.42, 'ACTIVE', NOW(), NOW(), 59.0707, 10.7555);
INSERT INTO item_images (item_id, image_url)
VALUES (32, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/recliner_dhjtyp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (8, 10, 'Sport Motorcycle', 'In excellent condition, barely used.', 1415.72, 'ACTIVE', NOW(), NOW(), 59.9698, 9.4122);
INSERT INTO item_images (item_id, image_url)
VALUES (33, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/motorcycle_ezkqih.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (7, 10, 'Helmet', 'Comes with original packaging.', 148.91, 'ACTIVE', NOW(), NOW(), 61.7991, 8.2558);
INSERT INTO item_images (item_id, image_url)
VALUES (34, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/helmet_awejnv.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 11, 'iPhone 14', 'Selling due to upgrade, works perfectly.', 1710.36, 'ACTIVE', NOW(), NOW(), 59.4015, 10.4854);
INSERT INTO item_images (item_id, image_url)
VALUES (35, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/iphone14_ccqzj0.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (4, 11, 'Google Pixel 9 Pro', 'Recently serviced and updated.', 860.25, 'ACTIVE', NOW(), NOW(), 62.0721, 5.9687);
INSERT INTO item_images (item_id, image_url)
VALUES (36, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/googlepixel9_i4lpdt.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 12, 'The Starry Night', 'Fully functional with no defects.', 950000, 'ACTIVE', NOW(), NOW(), 59.3042, 7.3704);
INSERT INTO item_images (item_id, image_url)
VALUES (37, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/oilpainting_tfvwyg.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (2, 12, 'Sketchbook Set', 'Comes with original packaging.', 87.90, 'ACTIVE', NOW(), NOW(), 60.5015, 6.1922);
INSERT INTO item_images (item_id, image_url)
VALUES (38, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sketchbook_set_aaefwj.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at, latitude, longitude)
VALUES (1, 12, 'Digital Drawing Tablet', 'Great for both home and travel use.', 934.66, 'ACTIVE', NOW(), NOW(), 61.1021, 5.3025);
INSERT INTO item_images (item_id, image_url)
VALUES (39, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217992/digitaldrawingtablet_dzx9c3.jpg');

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
