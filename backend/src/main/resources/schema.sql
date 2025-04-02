CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
email VARCHAR(255) NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
role ENUM('USER', 'ADMIN') DEFAULT 'USER',
display_name VARCHAR(100) NOT NULL,
first_name VARCHAR(100),
last_name VARCHAR(100),
phone VARCHAR(50),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
description TEXT,
parent_id INT,
FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE items (
id INT AUTO_INCREMENT PRIMARY KEY,
seller_id INT NOT NULL,
category_id INT NOT NULL,
brief_description VARCHAR(255) NOT NULL,
full_description TEXT,
price DECIMAL(10,2) NOT NULL,
latitude DECIMAL(9,6), -- Necessary?
longitude DECIMAL(9,6), -- Necessary?
status ENUM('ACTIVE', 'ARCHIVED', 'SOLD', 'RESERVED') DEFAULT 'ACTIVE',
published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (seller_id) REFERENCES users(id),
FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE item_images (
 id INT AUTO_INCREMENT PRIMARY KEY,
 item_id INT NOT NULL,
 image_url VARCHAR(255) NOT NULL,
 position INT DEFAULT 0,
 FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE favorites (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT NOT NULL,
item_id INT NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
UNIQUE KEY (user_id, item_id),
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (item_id) REFERENCES items(id)
);

-- Assumes each message references a specific item that's being discussed
CREATE TABLE messages (
id INT AUTO_INCREMENT PRIMARY KEY,
sender_id INT NOT NULL,
receiver_id INT NOT NULL,
item_id INT,  -- Optional: for context if message relates to an item
content TEXT NOT NULL,
sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (sender_id) REFERENCES users(id),
FOREIGN KEY (receiver_id) REFERENCES users(id),
FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE orders (
id INT AUTO_INCREMENT PRIMARY KEY,
buyer_id INT NOT NULL,
seller_id INT NOT NULL,
item_id INT NOT NULL,
order_status ENUM('PENDING', 'COMPLETED', 'CANCELED') DEFAULT 'PENDING',
price DECIMAL(10,2) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (buyer_id) REFERENCES users(id),
FOREIGN KEY (seller_id) REFERENCES users(id),
FOREIGN KEY (item_id) REFERENCES items(id)
);









