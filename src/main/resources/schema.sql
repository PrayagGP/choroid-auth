-- Simple database schema for Choroid Authentication Service
-- Single credentials table with username and password

-- Drop table if exists (for development)
DROP TABLE IF EXISTS credentials;

-- Create simple credentials table
CREATE TABLE credentials (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL
);

-- Insert sample users for testing
-- admin password: admin123  
-- testuser password: password123
INSERT INTO credentials (username, password) VALUES 
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCO.Oj5.2.wGT4O8oNPe0UiSfQRNPQZsG'),
('testuser', '$2a$12$8Nx8jF.YhZKKTgUW6tKq5O5FfFGT.SsUgQ2GjGJmLsIqYwSUgGFK');
