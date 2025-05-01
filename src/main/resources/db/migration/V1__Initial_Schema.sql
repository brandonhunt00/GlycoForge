-- V1__Initial_Schema.sql
-- Create initial tables for GlycoForge

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    age INT NULL,
    height_cm INT NULL,
    weight_kg DOUBLE NULL,
    insulin_ratio DOUBLE NULL COMMENT 'Units of insulin per 10g of carbs',
    sensitivity_factor DOUBLE NULL COMMENT 'How much 1 unit lowers BG (mg/dL or mmol/L)'
);

-- Meals table
CREATE TABLE meals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    description TEXT NULL,
    grams_carbs DOUBLE NULL,
    grams_protein DOUBLE NULL,
    grams_fat DOUBLE NULL,
    eaten_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Injections table
CREATE TABLE injections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    units DOUBLE NOT NULL,
    injected_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_meals_user_id_eaten_at ON meals(user_id, eaten_at DESC);
CREATE INDEX idx_injections_user_id_injected_at ON injections(user_id, injected_at DESC);

