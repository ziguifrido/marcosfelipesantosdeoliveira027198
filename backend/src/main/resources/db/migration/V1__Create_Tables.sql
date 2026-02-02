-- Migration: V1__Create_Tables.sql
-- Description: Creates all database tables for the discography application

-- Create Users table for JWT authentication
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Create Artist table
CREATE TABLE IF NOT EXISTS artist (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    profile_image_bucket VARCHAR(255),
    profile_image_object_key VARCHAR(255),
    profile_image_content_type VARCHAR(255)
);

-- Create Album table
CREATE TABLE IF NOT EXISTS album (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_date DATE,
    cover_bucket VARCHAR(255),
    cover_object_key VARCHAR(255),
    cover_content_type VARCHAR(255)
);

-- Create junction table for Artist-Album many-to-many relationship
-- Hibernate expects album_artists (alphabetical order by default)
CREATE TABLE IF NOT EXISTS album_artists (
    album_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    PRIMARY KEY (album_id, artist_id),
    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_artist_name ON artist(name);
CREATE INDEX IF NOT EXISTS idx_album_title ON album(title);
CREATE INDEX IF NOT EXISTS idx_album_release_date ON album(release_date);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
