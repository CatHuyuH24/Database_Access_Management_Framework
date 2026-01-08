-- Initialize DAM Demo Database

USE dam_demo;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255),
    age INT,
    status VARCHAR(50) DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Orders table (for sequence demo - though MySQL uses AUTO_INCREMENT)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    total_amount DOUBLE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Sessions table (UUID demo)
CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data
INSERT INTO users (username, email, age, status) VALUES
    ('admin', 'admin@dam-framework.com', 30, 'active'),
    ('demo_user', 'demo@dam-framework.com', 25, 'active');

INSERT INTO products (name, price) VALUES
    ('Sample Laptop', 999.99),
    ('Sample Mouse', 19.99),
    ('Sample Keyboard', 49.99);

-- Display initialization success
SELECT 'Database initialized successfully!' AS status;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS product_count FROM products;
