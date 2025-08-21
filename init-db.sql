-- Database initialization script for Billing Application
-- This script will be executed when the PostgreSQL container starts

-- Create database if it doesn't exist (handled by POSTGRES_DB environment variable)
-- CREATE DATABASE IF NOT EXISTS billing_db;

-- Create user if it doesn't exist (handled by POSTGRES_USER environment variable)
-- CREATE USER IF NOT EXISTS billing_user WITH PASSWORD 'billing_password';

-- Grant privileges (handled automatically by PostgreSQL Docker image)
-- GRANT ALL PRIVILEGES ON DATABASE billing_db TO billing_user;

-- Set timezone
SET timezone = 'UTC';

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- You can add any additional initialization SQL here
-- For example, creating indexes, setting up initial data, etc.

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'Billing database initialized successfully';
END $$;
