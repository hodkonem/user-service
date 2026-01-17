--liquibase formatted sql

--changeset hodkonem:001-create-user-table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       age INT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);