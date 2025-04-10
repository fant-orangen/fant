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
