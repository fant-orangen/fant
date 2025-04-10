-- The BCryptPasswordEncoder has encoded the password 'password123'
INSERT INTO users (email, display_name, password_hash)
VALUES ('alice@example.com', 'alice', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash, role)
VALUES ('jakob@mail.com', 'jakob', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2', 'ADMIN');

INSERT INTO users (email, display_name, password_hash)
VALUES ('ola@norge.no', 'ola', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('mariah@example.com', 'mariah', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('kevinh@example.com', 'kevinh', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('sofia@example.com', 'sofia', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('julian@example.com', 'julian', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('oliverb@example.com', 'oliverb', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('emilys@example.com', 'emilys', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('danielm@example.com', 'danielm', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('lucia@example.com', 'lucia', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('noahk@example.com', 'noahk', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

INSERT INTO users (email, display_name, password_hash)
VALUES ('ameliaw@example.com', 'ameliaw', '$2a$12$LoAYy4FBlI/QeeJ6ZsmXHuOhp4irHJjPhsLiELCjZJekQLiicPbL2');

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

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, status, published_at, updated_at,
                   latitude, longitude)
VALUES (1, 1, 'Test', 'Test', 500.00, 'ACTIVE', NOW(), NOW(), 62, 5.7);

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

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (1, 1, 'Durable hiking backpack',
        'This sturdy 65L hiking backpack includes a built-in rain cover, padded straps, and multiple compartments. Perfect for multiday trips.',
        450.00, 59.913900, 10.752200, 'ACTIVE', '2024-12-26 03:27:39', '2025-01-04 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (10, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217389/backpack_lzwcps.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (2, 1, 'Waterproof hiking boots',
        'A pair of waterproof hiking boots with high ankle support and Vibram soles. Great traction in snowy and rocky terrain.',
        1200.00, 60.393990, 5.324150, 'ACTIVE', '2025-01-18 03:27:39', '2025-01-28 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (11, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/hikingboots_ejkkus.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (3, 1, 'Hard shell suitcase set',
        'Three-piece suitcase set with spinner wheels, TSA locks, and expandable storage. Only used once on a holiday trip.',
        1000.00, 63.430200, 10.395100, 'ACTIVE', '2025-02-04 03:27:39', '2025-02-08 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (12, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/suitcase_set_ce8ui4.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (4, 2, 'High-speed blender',
        'Blend smoothies, soups, or even crush ice with this 1200W blender featuring stainless steel blades and 2L jug.',
        700.00, 58.969480, 5.733110, 'ACTIVE', '2024-12-13 03:27:39', '2024-12-22 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (13, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/blender_cca71d.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (5, 2, 'Compact digital microwave',
        'Compact 800W microwave with preset programs, defrost setting, and child safety lock. Ideal for student kitchens.',
        900.00, 69.649200, 18.956000, 'ACTIVE', '2025-01-05 03:27:39', '2025-01-15 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (14, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/microwave_i4o9ty.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (6, 2, 'Freestanding dishwasher',
        'Energy-efficient dishwasher with room for 12 place settings. Includes quick-wash and eco modes. Clean and reliable.',
        3000.00, 59.134180, 10.216650, 'ACTIVE', '2024-12-19 03:27:39', '2024-12-29 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (15, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/dishwasher_g7osez.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (7, 3, 'Aluminum fishing boat',
        'Lightweight 14ft aluminum boat with two bench seats. Includes oars and anchor. Used sparingly on a lake.',
        35000.00, 59.262500, 10.407600, 'ACTIVE', '2025-01-26 03:27:39', '2025-02-04 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (16, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/fishing_boat_axttfi.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (8, 3, '2-person inflatable kayak',
        'Stable and durable inflatable kayak, great for rivers and lakes. Packs into a backpack. Includes pump and paddles.',
        2500.00, 60.794500, 11.068600, 'ACTIVE', '2024-12-28 03:27:39', '2025-01-06 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (17, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/inflatable_kayak_jrcngn.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (9, 4, 'Set of 5 fantasy novels',
        'Complete fantasy saga with five hardcover volumes in near-mint condition. Great for fans of epic storytelling.',
        300.00, 59.756800, 10.221800, 'ACTIVE', '2025-01-09 03:27:39', '2025-01-18 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (18, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/fantasynovels_ygootf.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (10, 4, 'Modern Python guide',
        'Detailed Python 3 book covering topics from basics to advanced async programming. Like new, no markings inside.',
        400.00, 60.473000, 8.468900, 'ACTIVE', '2024-12-09 03:27:39', '2024-12-16 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (19, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/programmingbook_zsm3ur.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (1, 5, 'Professional DSLR camera',
        'Canon EOS 80D DSLR with 18-135mm lens. Well maintained, perfect for hobby photographers or beginners upgrading from compact cameras.',
        6000.00, 59.913900, 10.754200, 'ACTIVE', '2024-12-11 03:27:39', '2024-12-15 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (20, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/dslr_vpnsff.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (2, 5, 'Sturdy aluminum tripod',
        'Adjustable aluminum tripod with fluid head. Extends up to 160cm. Great for both video and still photography.',
        300.00, 60.392990, 5.324350, 'ACTIVE', '2025-01-14 03:27:39', '2025-01-20 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (21, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/tripod_stand_bgtupf.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (3, 6, 'Reliable used sedan',
        '2010 Toyota Avensis, 185,000 km. Regularly serviced, winter tires included. A dependable ride with automatic transmission.',
        45000.00, 63.432500, 10.395100, 'ACTIVE', '2025-02-01 03:27:39', '2025-02-10 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (22, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sedan_w4x7ii.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (4, 6, 'Wall-mounted EV charger',
        '7.2kW home charger for electric vehicles. Type 2 plug with 5m cable. Weather resistant, includes wall bracket.',
        5500.00, 58.969980, 5.734110, 'ACTIVE', '2024-11-22 03:27:39', '2024-11-27 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (23, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/carcharger_j3ka4b.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (5, 6, 'Spacious roof cargo box',
        'Thule Motion XT roof box with 450L capacity. Aerodynamic, dual-side opening, and central locking system.',
        2000.00, 69.648600, 18.956000, 'ACTIVE', '2024-12-30 03:27:39', '2025-01-03 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (24, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/car_roof_box_dcrmcp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (6, 7, 'Genuine leather jacket',
        'Men’s medium-sized black leather jacket. Soft lining and strong zippers. Hardly worn, stylish and warm.',
        1200.00, 59.131180, 10.216750, 'ACTIVE', '2025-01-02 03:27:39', '2025-01-10 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (25, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/leatherjacket_aakts3.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (7, 7, 'Floral summer dress',
        'Women’s size small floral dress from H&M. Lightweight cotton, ideal for warm weather. Worn a few times.',
        400.00, 59.267500, 10.408600, 'ACTIVE', '2025-01-09 03:27:39', '2025-01-13 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (26, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/summerdress_ijyeg2.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (8, 8, 'Powerful gaming laptop',
        'ASUS ROG Zephyrus with RTX 3060, 16GB RAM, 1TB SSD. Handles modern games with ease. Lightly used.', 15000.00,
        60.794500, 11.066600, 'ACTIVE', '2024-12-14 03:27:39', '2024-12-24 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (27, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/gaminglaptop_ihp4gq.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (9, 8, 'Custom-built PC tower',
        'Ryzen 7 5800X, RTX 3070, 32GB RAM, 1TB NVMe SSD. Built in 2023. Silent case with RGB lighting.', 12000.00,
        59.756800, 10.220900, 'ACTIVE', '2024-12-01 03:27:39', '2024-12-11 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (28, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/desktoppc_xggmrk.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (10, 8, '27-inch IPS monitor',
        'Dell UltraSharp 27” IPS display, 1440p resolution. Vivid color accuracy, ideal for design or gaming.', 2000.00,
        60.472000, 8.467900, 'ACTIVE', '2025-01-20 03:27:39', '2025-01-28 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (29, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/monitor_tc8ibs.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (1, 9, 'Solid oak dining table',
        'Extendable oak dining table seats up to 8 people. Lightly used, includes two matching chairs. Natural wood finish.',
        3500.00, 59.913200, 10.752200, 'ACTIVE', '2024-12-25 03:27:39', '2025-01-04 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (30, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/diningtable_rh9ane.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (2, 9, 'Tall wooden bookshelf',
        'IKEA Hemnes bookshelf with 5 adjustable shelves. Minimal wear, perfect for storing books, decor, or media.',
        1000.00, 60.392390, 5.324150, 'ACTIVE', '2024-11-20 03:27:39', '2024-11-29 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (31, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/bookshelf_z9fsqt.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (3, 9, 'Comfortable recliner chair',
        'Gray recliner with manual tilt function. Soft upholstery and lumbar support. Pet-free, smoke-free home.',
        2500.00, 63.430600, 10.395100, 'ACTIVE', '2025-01-16 03:27:39', '2025-01-21 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (32, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/recliner_dhjtyp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (4, 10, 'Yamaha MT-07 motorcycle',
        '2017 Yamaha MT-07, 28,000 km. Powerful engine, excellent condition. Recently serviced. Includes heated grips.',
        60000.00, 58.969880, 5.733110, 'ACTIVE', '2024-12-12 03:27:39', '2024-12-19 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (33, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/motorcycle_ezkqih.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (5, 10, 'Motorcycle helmet with visor',
        'HJC full-face helmet, size M, matte black. Clean and undamaged. Comes with carrying bag and extra visor.',
        1500.00, 69.649600, 18.956200, 'ACTIVE', '2024-11-30 03:27:39', '2024-12-05 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (34, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/helmet_awejnv.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (6, 11, 'Apple iPhone 14, 128GB',
        'Purchased new in 2023. Midnight color, includes original box and charger. Battery health at 95%.', 11000.00,
        59.131180, 10.216350, 'ACTIVE', '2024-12-03 03:27:39', '2024-12-08 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (35, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/iphone14_ccqzj0.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (7, 11, 'Pixel 9 Pro smartphone',
        'Latest Android flagship, 256GB version. Pristine screen, fast performance, excellent camera. Factory reset.',
        10000.00, 59.267500, 10.407200, 'ACTIVE', '2024-12-15 03:27:39', '2024-12-26 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (36, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/googlepixel9_i4lpdt.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (8, 12, 'Replica art canvas',
        'High-quality replica of Van Gogh The Starry Night. 70x50cm stretched canvas on wood frame. Looks fantastic.',
        250.00, 60.794500, 11.067900, 'ACTIVE', '2024-12-08 03:27:39', '2024-12-19 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (37, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/vangogh_replica_ynafdl.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (9, 12, 'Artist''s sketchbook set',
        'Bundle includes 3 hardcover sketchbooks (A4 size), smooth paper for graphite and ink. Unused, still wrapped.',
        200.00, 59.756800, 10.230900, 'ACTIVE', '2025-01-11 03:27:39', '2025-01-16 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (38, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sketchbook_set_aaefwj.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status,
                   published_at, updated_at)
VALUES (10, 12, 'Wacom Intuos Pro M',
        'Pen and touch drawing tablet with pressure-sensitive stylus. Ideal for digital art and photo editing.',
        4000.00, 60.472000, 8.468400, 'ACTIVE', '2024-12-17 03:27:39', '2024-12-29 03:27:39');
INSERT INTO item_images (item_id, image_url)
VALUES (39, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217992/digitaldrawingtablet_dzx9c3.jpg');


-- Copied items
INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 1, 'Black Trekking Backpack 55L', 'Lightweight and durable backpack designed for weekend hikes and longer excursions. Includes hip belt and rain cover.', 479.00, 60.0123, 10.7456, 'ACTIVE', '2024-12-29 12:14:00', '2025-01-03 09:45:12');
INSERT INTO item_images (item_id, image_url)
VALUES (40, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217389/backpack_lzwcps.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 1, 'Mountain Boots - Size 42', 'Sturdy boots with waterproof membrane and reinforced toes. Great for rough Norwegian terrain. Used twice.', 1145.00, 60.3852, 5.3344, 'ACTIVE', '2025-01-02 14:30:10', '2025-01-07 11:12:55');
INSERT INTO item_images (item_id, image_url)
VALUES (41, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/hikingboots_ejkkus.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 1, 'Spinner Suitcase (Large)', 'Large black suitcase with 360° wheels and built-in combination lock. Some scratches but fully functional.', 890.00, 63.4221, 10.3978, 'ACTIVE', '2024-12-21 17:05:00', '2025-01-04 12:20:01');
INSERT INTO item_images (item_id, image_url)
VALUES (42, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/suitcase_set_ce8ui4.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 2, 'Kenwood Smoothie Blender', 'Compact 1.5L blender, great for shakes and sauces. Includes two bottles. In good condition, lightly used.', 549.00, 58.9771, 5.7320, 'ACTIVE', '2025-01-05 08:44:33', '2025-01-10 14:22:11');
INSERT INTO item_images (item_id, image_url)
VALUES (43, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/blender_cca71d.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 2, 'Samsung Microwave - 20L', 'Modern digital microwave with child lock and quick defrost. Good condition with minor wear.', 850.00, 69.6521, 18.9500, 'ACTIVE', '2024-12-27 10:11:50', '2025-01-08 15:05:34');
INSERT INTO item_images (item_id, image_url)
VALUES (44, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/microwave_i4o9ty.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 2, 'Whirlpool Dishwasher', 'Freestanding model with eco mode and silent wash. Cleans up to 13 place settings. Recently cleaned.', 2890.00, 59.1340, 10.2141, 'ACTIVE', '2025-01-01 09:12:12', '2025-01-06 17:03:49');
INSERT INTO item_images (item_id, image_url)
VALUES (45, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/dishwasher_g7osez.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 3, 'Rowing Boat - Aluminum', '13-foot aluminium boat, ideal for lakes and calm rivers. Two seats, basic oars included.', 34250.00, 59.2611, 10.4010, 'ACTIVE', '2024-12-23 18:01:29', '2025-01-03 08:40:00');
INSERT INTO item_images (item_id, image_url)
VALUES (46, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/fishing_boat_axttfi.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 3, 'Inflatable Kayak Duo', 'Two-person inflatable kayak with pump and repair kit. Great for beginners. Packed into a carry bag.', 2390.00, 60.7910, 11.0701, 'ACTIVE', '2025-01-03 10:10:10', '2025-01-08 11:11:11');
INSERT INTO item_images (item_id, image_url)
VALUES (47, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/inflatable_kayak_jrcngn.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 4, 'Fantasy Book Bundle', 'Includes “The Final Empire”, “The Way of Kings” and 3 other titles. Paperback. Excellent condition.', 310.00, 59.7590, 10.2210, 'ACTIVE', '2024-12-28 07:00:00', '2025-01-04 07:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (48, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/fantasynovels_ygootf.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 4, 'Python Programming - 2nd Ed.', 'Comprehensive guide to Python 3. Covers OOP, APIs, Django, and testing. No annotations or damage.', 380.00, 60.4700, 8.4650, 'ACTIVE', '2024-12-20 11:45:25', '2024-12-30 14:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (49, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/programmingbook_zsm3ur.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 5, 'Canon EOS Camera w/ Kit Lens', 'Canon EOS 2000D DSLR camera with 18–55mm kit lens. Barely used and in great condition. Ideal for beginners.', 5700.00, 59.9125, 10.7480, 'ACTIVE', '2025-01-03 09:22:10', '2025-01-08 14:45:32');
INSERT INTO item_images (item_id, image_url)
VALUES (50, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/dslr_vpnsff.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 5, 'Velbon Travel Tripod', 'Lightweight aluminium tripod, folds down to 35cm. Great for hiking or compact travel photography.', 280.00, 60.3951, 5.3200, 'ACTIVE', '2025-01-05 13:10:00', '2025-01-09 16:01:44');
INSERT INTO item_images (item_id, image_url)
VALUES (51, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/tripod_stand_bgtupf.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 6, 'Volkswagen Golf 2012', '1.6 TDI Golf with 165,000 km. Manual transmission, heated seats, and recently changed timing belt.', 46800.00, 63.4291, 10.3998, 'ACTIVE', '2024-12-30 17:55:00', '2025-01-07 10:10:10');
INSERT INTO item_images (item_id, image_url)
VALUES (52, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sedan_w4x7ii.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 6, 'Easee Home EV Charger', 'Wall-mounted 22kW charger for electric vehicles. Wi-Fi connected. Used for less than a year.', 5200.00, 58.9703, 5.7348, 'ACTIVE', '2025-01-04 08:00:00', '2025-01-09 12:15:00');
INSERT INTO item_images (item_id, image_url)
VALUES (53, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/carcharger_j3ka4b.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 6, 'Thule Roof Cargo Box 420L', 'Gloss black rooftop box. Aerodynamic, dual-sided opening. Great for ski trips or extra baggage.', 2100.00, 69.6510, 18.9571, 'ACTIVE', '2025-01-02 16:22:40', '2025-01-06 11:18:40');
INSERT INTO item_images (item_id, image_url)
VALUES (54, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/car_roof_box_dcrmcp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 7, 'Vintage Leather Jacket L', 'Dark brown leather jacket, men’s large. Worn look with character, no rips or major flaws.', 990.00, 59.1335, 10.2139, 'ACTIVE', '2025-01-06 11:11:11', '2025-01-10 17:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (55, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/leatherjacket_aakts3.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 7, 'Summer Dress (S)', 'Flowy summer dress with floral print, size small. Comfortable for warm days. Worn only once.', 420.00, 59.2650, 10.4055, 'ACTIVE', '2025-01-07 07:30:00', '2025-01-09 14:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (56, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/summerdress_ijyeg2.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 8, 'Acer Nitro 5 Gaming Laptop', '15.6” screen, GTX 1650 GPU, 512GB SSD, 8GB RAM. Ideal entry-level gaming system. Works flawlessly.', 8490.00, 60.7900, 11.0600, 'ACTIVE', '2025-01-03 10:20:20', '2025-01-06 13:33:33');
INSERT INTO item_images (item_id, image_url)
VALUES (57, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/gaminglaptop_ihp4gq.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 8, 'Ryzen Tower PC - 32GB RAM', 'Powerful desktop for gaming and content creation. RTX 3060, 1TB SSD, glass panel case with RGB.', 12700.00, 59.7591, 10.2234, 'ACTIVE', '2025-01-01 09:00:00', '2025-01-07 15:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (58, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/desktoppc_xggmrk.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 8, 'LG UltraFine 4K Monitor', '27-inch IPS monitor with USB-C input and 3840x2160 resolution. Excellent for design work.', 2990.00, 60.4731, 8.4671, 'ACTIVE', '2024-12-30 08:55:00', '2025-01-05 10:10:10');
INSERT INTO item_images (item_id, image_url)
VALUES (59, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/monitor_tc8ibs.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 9, 'Oak Dining Table with Bench', 'Rustic-style dining table in solid oak. Comes with one long bench. Seats 6 comfortably. Great for cabins.', 3750.00, 59.9112, 10.7510, 'ACTIVE', '2025-01-04 12:00:00', '2025-01-09 15:40:00');
INSERT INTO item_images (item_id, image_url)
VALUES (60, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/diningtable_rh9ane.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 9, 'White Bookshelf w/ Glass Doors', 'Tall bookshelf with frosted glass doors. Adjustable shelves, ideal for books or display items.', 1050.00, 60.3923, 5.3260, 'ACTIVE', '2025-01-05 10:10:00', '2025-01-07 16:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (61, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/bookshelf_z9fsqt.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 9, 'Electric Recliner Chair', 'Dark gray electric recliner with USB charging port. Purchased in 2022. In excellent working order.', 2850.00, 63.4321, 10.3966, 'ACTIVE', '2025-01-06 08:00:00', '2025-01-08 13:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (62, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/recliner_dhjtyp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 10, '2019 Yamaha Tracer 700', 'Sport touring motorcycle with only 19,000 km. Comfortable seat and tall windshield included.', 64000.00, 58.9680, 5.7302, 'ACTIVE', '2025-01-02 09:15:00', '2025-01-07 09:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (63, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/motorcycle_ezkqih.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 10, 'Shoei GT-Air 2 Helmet', 'Premium full-face helmet in matte blue. Pinlock visor, great ventilation. Barely used.', 1700.00, 69.6490, 18.9551, 'ACTIVE', '2025-01-03 10:00:00', '2025-01-06 17:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (64, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/helmet_awejnv.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 11, 'iPhone 14 – Midnight Black', 'Unlocked iPhone 14 (128GB). Always in case and screen protector. Selling due to upgrade.', 10750.00, 59.1320, 10.2150, 'ACTIVE', '2025-01-06 15:00:00', '2025-01-09 12:22:22');
INSERT INTO item_images (item_id, image_url)
VALUES (65, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/iphone14_ccqzj0.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 11, 'Pixel 9 Pro (256GB)', 'Stormy black Pixel 9 Pro with clean Android OS. Excellent camera. Barely used.', 9600.00, 59.2655, 10.4062, 'ACTIVE', '2024-12-29 14:20:00', '2025-01-05 10:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (66, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/googlepixel9_i4lpdt.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 12, 'Framed Van Gogh Replica', 'Museum-quality replica of “The Starry Night” in gold frame. Size 70x50cm. Adds style to any room.', 280.00, 60.7951, 11.0688, 'ACTIVE', '2025-01-01 16:00:00', '2025-01-07 09:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (67, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/oilpainting_tfvwyg.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 12, 'Sketchbook Bundle A4', 'Three 120gsm A4 sketchbooks with durable covers. Great for pencil, ink, and charcoal.', 210.00, 59.7569, 10.2240, 'ACTIVE', '2025-01-05 12:12:00', '2025-01-08 18:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (68, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sketchbook_set_aaefwj.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 12, 'Wacom Pro Medium Tablet', 'Drawing tablet with pen and multi-touch support. Works perfectly. Box and cable included.', 3850.00, 60.4721, 8.4692, 'ACTIVE', '2024-12-27 08:45:00', '2025-01-03 16:15:00');
INSERT INTO item_images (item_id, image_url)
VALUES (69, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217992/digitaldrawingtablet_dzx9c3.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 1, 'Hiking Daypack - 35L', 'Compact hiking daypack with padded back and hydration bladder slot. Ideal for one-day hikes and travel.', 399.00, 59.9120, 10.7501, 'ACTIVE', '2025-01-03 09:00:00', '2025-01-08 13:20:00');
INSERT INTO item_images (item_id, image_url)
VALUES (70, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217389/backpack_lzwcps.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 1, 'Winter Hiking Boots – Size 43', 'Insulated and waterproof boots, perfect for snowy trails. Used a handful of times, no damage.', 1180.00, 60.3910, 5.3255, 'ACTIVE', '2025-01-02 14:30:00', '2025-01-06 12:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (71, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/hikingboots_ejkkus.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 1, 'Medium Check-in Suitcase', 'Black medium-sized suitcase with smooth rolling wheels and expandable zipper. Works perfectly.', 875.00, 63.4310, 10.3960, 'ACTIVE', '2025-01-05 13:10:00', '2025-01-10 16:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (72, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/suitcase_set_ce8ui4.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 2, 'Philips 1000W Blender', 'Durable blender for smoothies and sauces. Includes one jar and a spare blade. Runs quietly.', 620.00, 58.9700, 5.7310, 'ACTIVE', '2025-01-01 10:15:00', '2025-01-07 15:35:00');
INSERT INTO item_images (item_id, image_url)
VALUES (73, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/blender_cca71d.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 2, 'Panasonic Microwave 25L', 'Basic white microwave with turntable and digital display. Clean and fully working. No dents.', 740.00, 69.6485, 18.9549, 'ACTIVE', '2025-01-04 17:00:00', '2025-01-08 19:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (74, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/microwave_i4o9ty.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 2, 'Siemens Dishwasher - White', 'Freestanding dishwasher with multiple settings. Cleans thoroughly, recently descaled. Slight cosmetic wear.', 2950.00, 59.1349, 10.2170, 'ACTIVE', '2025-01-06 08:30:00', '2025-01-10 11:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (75, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/dishwasher_g7osez.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 3, 'Aluminum Rowboat w/ Seats', '12ft aluminum rowboat with two bench seats. Lightweight and easy to maneuver. Oars included.', 32800.00, 59.2629, 10.4088, 'ACTIVE', '2025-01-02 07:45:00', '2025-01-06 13:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (76, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/fishing_boat_axttfi.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 3, 'Inflatable Kayak w/ Pump', 'Great for weekend lake trips. Packs small. Comes with double-action pump and two paddles.', 2450.00, 60.7956, 11.0681, 'ACTIVE', '2025-01-03 09:00:00', '2025-01-07 14:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (77, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/inflatable_kayak_jrcngn.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 4, 'Fantasy Hardcover Collection', 'Hardcover editions of 4 bestselling fantasy novels. Minimal wear. Great for collectors or gifting.', 340.00, 59.7588, 10.2231, 'ACTIVE', '2025-01-04 11:11:00', '2025-01-08 15:25:00');
INSERT INTO item_images (item_id, image_url)
VALUES (78, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/fantasynovels_ygootf.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 4, 'Learn Python 3 Fast – Book', 'A concise but complete guide to Python 3. Covers fundamentals, OOP, file handling, and more.', 390.00, 60.4715, 8.4677, 'ACTIVE', '2025-01-01 10:00:00', '2025-01-07 16:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (79, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/programmingbook_zsm3ur.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 5, 'Canon Rebel T7 Kit', 'Entry-level DSLR with 18–55mm lens. Includes charger, strap, and 32GB memory card. Barely used.', 5650.00, 59.9122, 10.7505, 'ACTIVE', '2025-01-02 12:30:00', '2025-01-07 16:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (80, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/dslr_vpnsff.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 5, 'Tripod for DSLR/Phone', 'Universal aluminum tripod with quick-release plate. Adjustable height. Includes phone adapter.', 315.00, 60.3930, 5.3220, 'ACTIVE', '2025-01-04 09:00:00', '2025-01-08 11:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (81, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/tripod_stand_bgtupf.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 6, 'Skoda Octavia 2015 - Diesel', 'Clean and well-maintained family car with 150,000 km. Tow bar, heated seats, and cruise control.', 47900.00, 63.4315, 10.3958, 'ACTIVE', '2025-01-03 14:10:00', '2025-01-07 12:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (82, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sedan_w4x7ii.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 6, 'Portable Wallbox EV Charger', '7.4kW portable charger with Type 2 connector. Weatherproof and reliable for home use.', 5400.00, 58.9701, 5.7325, 'ACTIVE', '2025-01-02 10:30:00', '2025-01-08 13:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (83, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/carcharger_j3ka4b.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 6, 'Thule Roof Box 500L', 'Large silver roof box with aerodynamic shape. Easy mounting system. Minor surface scuffs.', 2250.00, 69.6502, 18.9544, 'ACTIVE', '2025-01-05 08:45:00', '2025-01-09 14:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (84, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/car_roof_box_dcrmcp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 7, 'Real Leather Biker Jacket', 'Classic men’s biker jacket with zip cuffs and quilted lining. Heavy-duty and warm.', 1280.00, 59.1325, 10.2160, 'ACTIVE', '2025-01-06 10:00:00', '2025-01-10 15:20:00');
INSERT INTO item_images (item_id, image_url)
VALUES (85, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/leatherjacket_aakts3.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 7, 'H&M Sundress - Size M', 'Light and airy sundress with pastel floral pattern. Great condition. Zipper on side.', 410.00, 59.2681, 10.4091, 'ACTIVE', '2025-01-03 11:11:00', '2025-01-08 14:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (86, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/summerdress_ijyeg2.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 8, 'HP Omen Laptop - i7 + RTX 2060', 'Gaming laptop with 144Hz display, 512GB SSD, and 16GB RAM. Smooth gameplay and fast boot.', 10250.00, 60.7941, 11.0665, 'ACTIVE', '2025-01-02 15:00:00', '2025-01-07 17:25:00');
INSERT INTO item_images (item_id, image_url)
VALUES (87, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/gaminglaptop_ihp4gq.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 8, 'Ryzen 5 PC Tower - 16GB RAM', 'Custom-built PC with GTX 1660 Ti and 512GB SSD. Great for everyday use and gaming.', 8650.00, 59.7582, 10.2213, 'ACTIVE', '2025-01-01 09:30:00', '2025-01-06 13:40:00');
INSERT INTO item_images (item_id, image_url)
VALUES (88, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/desktoppc_xggmrk.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 8, 'BenQ Designer Monitor 27”', 'Color-calibrated monitor with USB-C and 1440p resolution. Slim bezels, VESA mountable.', 2890.00, 60.4722, 8.4695, 'ACTIVE', '2024-12-31 08:00:00', '2025-01-05 11:11:00');
INSERT INTO item_images (item_id, image_url)
VALUES (89, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/monitor_tc8ibs.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 9, 'Oak Dining Table with Chairs', 'Extendable oak dining table with seating for 6. Includes matching chairs. Excellent condition, used indoors only.', 3600.00, 59.9115, 10.7518, 'ACTIVE', '2025-01-02 12:00:00', '2025-01-06 15:20:00');
INSERT INTO item_images (item_id, image_url)
VALUES (90, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/diningtable_rh9ane.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 9, 'Minimalist White Bookshelf', 'Tall, slim bookshelf in matte white. 5 levels, suitable for tight spaces. IKEA Billy model.', 950.00, 60.3927, 5.3248, 'ACTIVE', '2025-01-04 08:30:00', '2025-01-07 12:40:00');
INSERT INTO item_images (item_id, image_url)
VALUES (91, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/bookshelf_z9fsqt.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 9, 'Recliner Chair with Footrest', 'Manual recliner with included footrest. Dark grey fabric. Perfect for reading or TV time.', 2650.00, 63.4312, 10.3962, 'ACTIVE', '2025-01-05 10:15:00', '2025-01-09 11:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (92, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/recliner_dhjtyp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 10, 'Honda CB500X 2020', 'Versatile adventure motorcycle with ABS. 24,000 km, always garaged. Comes with side luggage.', 58900.00, 58.9689, 5.7340, 'ACTIVE', '2025-01-03 09:30:00', '2025-01-08 13:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (93, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/motorcycle_ezkqih.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 10, 'AGV Full-Face Helmet - L', 'Sleek full-face helmet with excellent airflow. Size L. No scratches, visor crystal clear.', 1550.00, 69.6493, 18.9553, 'ACTIVE', '2025-01-06 14:45:00', '2025-01-10 16:15:00');
INSERT INTO item_images (item_id, image_url)
VALUES (94, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/helmet_awejnv.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 11, 'iPhone 14 - Product Red', 'Apple iPhone 14 in red, 128GB. Battery health 93%. Includes case, cable, and box.', 10890.00, 59.1329, 10.2172, 'ACTIVE', '2025-01-02 11:11:00', '2025-01-07 14:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (95, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/iphone14_ccqzj0.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 11, 'Pixel 9 Pro - Arctic White', 'Flagship Google phone, barely used. Amazing camera, 256GB storage. Charger included.', 9700.00, 59.2679, 10.4065, 'ACTIVE', '2025-01-04 10:00:00', '2025-01-08 16:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (96, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/googlepixel9_i4lpdt.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 12, 'The Starry Night Canvas', 'Large replica of Van Gogh’s iconic painting. Thick canvas wrap with vivid colors. No frame.', 295.00, 60.7947, 11.0682, 'ACTIVE', '2025-01-05 12:00:00', '2025-01-09 13:50:00');
INSERT INTO item_images (item_id, image_url)
VALUES (97, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/oilpainting_tfvwyg.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 12, 'Sketchpad Trio - Spiral Bound', 'Three A4 sketchpads with 100gsm paper. Slight bending on cover corners, inside pages clean.', 190.00, 59.7562, 10.2215, 'ACTIVE', '2025-01-01 10:25:00', '2025-01-06 13:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (98, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sketchbook_set_aaefwj.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 12, 'Wacom Drawing Tablet - Medium', 'Intuos Pro Medium tablet with stylus and USB cable. Excellent condition. Used for school projects.', 3950.00, 60.4725, 8.4683, 'ACTIVE', '2024-12-30 07:40:00', '2025-01-06 11:15:00');
INSERT INTO item_images (item_id, image_url)
VALUES (99, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217992/digitaldrawingtablet_dzx9c3.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 1, 'Compact Day Hike Backpack', 'Lightweight 30L daypack with water-resistant fabric. Good for short hikes or daily commuting. Slight wear.', 420.00, 59.9133, 10.7525, 'ACTIVE', '2025-01-02 09:15:00', '2025-01-06 13:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (100, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217389/backpack_lzwcps.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 1, 'Heavy-Duty Winter Boots (44)', 'Insulated leather hiking boots. Waterproof and warm, used on two trips. Vibram soles with excellent grip.', 1230.00, 60.3931, 5.3242, 'ACTIVE', '2025-01-03 10:00:00', '2025-01-07 12:40:00');
INSERT INTO item_images (item_id, image_url)
VALUES (101, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/hikingboots_ejkkus.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 1, 'Large Suitcase with TSA Lock', 'Robust travel suitcase with spinner wheels and coded TSA lock. A few scuffs from airport use.', 945.00, 63.4306, 10.3956, 'ACTIVE', '2025-01-04 12:00:00', '2025-01-08 15:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (102, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/suitcase_set_ce8ui4.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 2, 'Bosch Smoothie Blender 1.2L', 'Simple and effective blender with safety lid. Great for smoothies and sauces. Well-maintained.', 690.00, 58.9696, 5.7329, 'ACTIVE', '2025-01-01 08:45:00', '2025-01-06 11:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (103, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/blender_cca71d.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 2, 'Samsung Digital Microwave - 23L', 'Black microwave with digital display and rotating plate. Includes quick defrost and grill function.', 890.00, 69.6491, 18.9562, 'ACTIVE', '2025-01-03 11:30:00', '2025-01-06 14:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (104, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/microwave_i4o9ty.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 2, 'Electrolux Dishwasher - 45cm', 'Slim freestanding dishwasher, ideal for smaller kitchens. Runs quietly, includes eco mode.', 2790.00, 59.1345, 10.2159, 'ACTIVE', '2025-01-05 07:30:00', '2025-01-09 10:40:00');
INSERT INTO item_images (item_id, image_url)
VALUES (105, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/dishwasher_g7osez.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 3, '12ft Aluminium Row Boat', 'Classic aluminium boat with two seats and wooden oars. Easy to load onto trailer. Minor dings.', 33400.00, 59.2627, 10.4072, 'ACTIVE', '2025-01-04 16:00:00', '2025-01-08 12:20:00');
INSERT INTO item_images (item_id, image_url)
VALUES (106, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/fishing_boat_axttfi.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 3, 'Inflatable River Kayak', 'Inflatable single kayak for calm rivers and lakes. Comes with paddle and foot pump.', 2330.00, 60.7943, 11.0683, 'ACTIVE', '2025-01-06 09:45:00', '2025-01-10 13:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (107, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/inflatable_kayak_jrcngn.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 4, '5 Book Fantasy Set - Boxed', 'Fantasy series boxed set, all hardcover. Like new, no dust jacket damage. Local pickup preferred.', 330.00, 59.7578, 10.2233, 'ACTIVE', '2025-01-02 15:10:00', '2025-01-07 12:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (108, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/fantasynovels_ygootf.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 4, 'Python Crash Course – 3rd Ed.', 'Excellent beginner Python book. Clean copy, no writing inside. Includes intro to web dev and games.', 410.00, 60.4726, 8.4687, 'ACTIVE', '2025-01-01 09:00:00', '2025-01-06 13:15:00');
INSERT INTO item_images (item_id, image_url)
VALUES (109, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/programmingbook_zsm3ur.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 5, 'Canon 250D DSLR + Lens', 'Lightweight DSLR perfect for learning photography. Includes Canon 18–55mm lens, strap, and 64GB card.', 5850.00, 59.9130, 10.7512, 'ACTIVE', '2025-01-03 10:15:00', '2025-01-07 12:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (110, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/dslr_vpnsff.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 5, 'Aluminum Tripod - Tall Model', 'Sturdy aluminum tripod extending to 175cm. Fluid head, quick release plate, and carrying case included.', 350.00, 60.3928, 5.3233, 'ACTIVE', '2025-01-04 11:10:00', '2025-01-08 13:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (111, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/tripod_stand_bgtupf.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 6, 'Peugeot 308 2016 - Diesel', 'Compact and efficient car with automatic gearbox. 132,000 km. Well maintained with full service history.', 49500.00, 63.4308, 10.3961, 'ACTIVE', '2025-01-02 12:45:00', '2025-01-06 15:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (112, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sedan_w4x7ii.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 6, 'Zaptec Go Home Charger', 'Compact smart charger for EVs. Wi-Fi enabled, sleek black design. Used for 8 months.', 5250.00, 58.9695, 5.7337, 'ACTIVE', '2025-01-03 08:00:00', '2025-01-08 14:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (113, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/carcharger_j3ka4b.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 6, 'Roof Cargo Box – Matte Grey', 'Thule Touring model with 400L capacity. Secure locking and simple roof bar attachment system.', 2180.00, 69.6494, 18.9563, 'ACTIVE', '2025-01-01 13:30:00', '2025-01-06 11:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (114, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/car_roof_box_dcrmcp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 7, 'Sheepskin Leather Jacket (L)', 'Warm lined leather jacket with interior pockets and button cuffs. Used but in good condition.', 1150.00, 59.1319, 10.2167, 'ACTIVE', '2025-01-02 15:15:00', '2025-01-07 12:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (115, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/leatherjacket_aakts3.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 7, 'Casual Summer Dress - Medium', 'Soft cotton summer dress with floral pattern. Size medium. No signs of wear, like new.', 430.00, 59.2672, 10.4068, 'ACTIVE', '2025-01-04 09:30:00', '2025-01-09 14:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (116, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/summerdress_ijyeg2.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 8, 'ASUS TUF Gaming Laptop', '15.6” FHD screen, Ryzen 5 CPU, GTX 1650 graphics. Great for esports titles and school.', 7890.00, 60.7940, 11.0679, 'ACTIVE', '2025-01-03 13:20:00', '2025-01-07 17:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (117, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/gaminglaptop_ihp4gq.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 8, 'Workstation PC Tower - 32GB', 'Silent tower PC with Ryzen 9, 32GB RAM and RTX 3080. Great for creative work and VR.', 16490.00, 59.7571, 10.2202, 'ACTIVE', '2025-01-02 10:00:00', '2025-01-07 12:20:00');
INSERT INTO item_images (item_id, image_url)
VALUES (118, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/desktoppc_xggmrk.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 8, 'Philips 32” Monitor – 4K UHD', '32-inch 4K UHD IPS monitor. Great color accuracy and adjustable stand. No dead pixels.', 3150.00, 60.4723, 8.4691, 'ACTIVE', '2025-01-01 11:00:00', '2025-01-06 15:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (119, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/monitor_tc8ibs.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (1, 9, 'Rustic Dining Table - 6 Seats', 'Beautiful wood dining table with minor scratches. 180cm length. Seats 6 comfortably.', 3450.00, 59.9136, 10.7526, 'ACTIVE', '2025-01-03 08:30:00', '2025-01-08 13:15:00');
INSERT INTO item_images (item_id, image_url)
VALUES (120, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/diningtable_rh9ane.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (2, 9, 'Black Metal Bookshelf', 'Minimalist bookshelf made of metal with open sides. 5 shelves. Very sturdy and stylish.', 880.00, 60.3925, 5.3250, 'ACTIVE', '2025-01-02 10:00:00', '2025-01-06 16:10:00');
INSERT INTO item_images (item_id, image_url)
VALUES (121, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217207/bookshelf_z9fsqt.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (3, 9, 'Grey Recliner w/ Massage', 'Electric recliner with heating and massage. Remote included. Upholstery in great shape.', 2950.00, 63.4309, 10.3959, 'ACTIVE', '2025-01-04 09:45:00', '2025-01-09 11:20:00');
INSERT INTO item_images (item_id, image_url)
VALUES (122, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/recliner_dhjtyp.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (4, 10, 'KTM Duke 390 2021', 'Nimble and fast city bike with ABS and TFT screen. 12,000 km. Chain recently replaced.', 51500.00, 58.9694, 5.7333, 'ACTIVE', '2025-01-05 10:30:00', '2025-01-09 14:55:00');
INSERT INTO item_images (item_id, image_url)
VALUES (123, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/motorcycle_ezkqih.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (5, 10, 'Shark Evo Helmet (Flip-Up)', 'Convertible full-face helmet with sun visor. Gloss black finish. Clean with no scratches.', 1650.00, 69.6492, 18.9566, 'ACTIVE', '2025-01-01 12:00:00', '2025-01-06 15:45:00');
INSERT INTO item_images (item_id, image_url)
VALUES (124, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/helmet_awejnv.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (6, 11, 'iPhone 14 – Starlight', 'Like-new condition, 128GB. Purchased late 2023. Comes with original box, no visible wear.', 10950.00, 59.1317, 10.2164, 'ACTIVE', '2025-01-03 14:00:00', '2025-01-07 11:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (125, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217209/iphone14_ccqzj0.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (7, 11, 'Google Pixel 9 Pro 5G', 'Like-new Pixel phone. 6.7” display, clean OS, excellent camera. Includes charger and box.', 9850.00, 59.2677, 10.4073, 'ACTIVE', '2025-01-02 15:45:00', '2025-01-06 13:25:00');
INSERT INTO item_images (item_id, image_url)
VALUES (126, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217208/googlepixel9_i4lpdt.avif');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (8, 12, 'Framed Starry Night Replica', 'Hand-stretched canvas in a wooden black frame. Bright colors, no scratches. Looks great on a wall.', 310.00, 60.7946, 11.0677, 'ACTIVE', '2025-01-05 13:00:00', '2025-01-09 10:30:00');
INSERT INTO item_images (item_id, image_url)
VALUES (127, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/oilpainting_tfvwyg.webp');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (9, 12, 'Sketchbook Set – Mixed Media', 'Three hardcover A4 sketchbooks. Paper handles watercolor, ink, pencil. Slightly dusty from storage.', 215.00, 59.7565, 10.2222, 'ACTIVE', '2025-01-04 11:30:00', '2025-01-08 14:00:00');
INSERT INTO item_images (item_id, image_url)
VALUES (128, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217210/sketchbook_set_aaefwj.jpg');

INSERT INTO items (seller_id, category_id, brief_description, full_description, price, latitude, longitude, status, published_at, updated_at)
VALUES (10, 12, 'Intuos Pro M – Boxed', 'Wacom tablet with stylus and USB cable. Original packaging included. Fully functional and clean.', 3990.00, 60.4727, 8.4686, 'ACTIVE', '2025-01-01 10:30:00', '2025-01-06 12:50:00');
INSERT INTO item_images (item_id, image_url)
VALUES (129, 'https://res.cloudinary.com/dsa5d788x/image/upload/v1744217992/digitaldrawingtablet_dzx9c3.jpg');

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
