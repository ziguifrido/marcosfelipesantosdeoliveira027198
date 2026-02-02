-- Migration: V2__Default_Users_and_Sample_Data.sql
-- Description: Creates default users (admin and user) and sample data with Linkin Park similar bands

-- =====================================================
-- DEFAULT USERS
-- =====================================================

-- Create admin user (password: admin123)
INSERT INTO users (id, username, password, role)
VALUES (
    gen_random_uuid(),
    'admin',
    '$2a$12$xGdl91X44ibNU/ezKoFQoeIf0aspmcbrT2/jH7FSFSJcgezPYw1YC',
    'ADMIN'
);

-- Create regular user (password: user123)
INSERT INTO users (id, username, password, role)
VALUES (
    gen_random_uuid(),
    'user',
    '$2a$12$aW0RtpAsTKhGJDtzzLhlyuMH8fVv7J1hhji2CqE0BrTHuq78fbFn.',
    'USER'
);

-- =====================================================
-- SAMPLE ARTISTS - Linkin Park Similar Bands
-- =====================================================

-- Linkin Park
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'Linkin Park', 'Alternative Rock / Nu Metal', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440000.jpg', 'image/jpeg');

-- Breaking Benjamin
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Breaking Benjamin', 'Alternative Rock / Post-Grunge', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440001.jpg', 'image/jpeg');

-- Three Days Grace
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'Three Days Grace', 'Alternative Rock / Post-Grunge', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440002.jpg', 'image/jpeg');

-- Seether
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440003', 'Seether', 'Alternative Rock / Post-Grunge', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440003.jpg', 'image/jpeg');

-- Disturbed
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440004', 'Disturbed', 'Heavy Metal / Alternative Metal', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440004.jpg', 'image/jpeg');

-- Chevelle
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440005', 'Chevelle', 'Alternative Rock / Hard Rock', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440005.jpg', 'image/jpeg');

-- Staind
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440006', 'Staind', 'Alternative Rock / Nu Metal', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440006.jpg', 'image/jpeg');

-- Papa Roach
INSERT INTO artist (id, name, genre, profile_image_bucket, profile_image_object_key, profile_image_content_type)
VALUES ('550e8400-e29b-41d4-a716-446655440007', 'Papa Roach', 'Alternative Rock / Nu Metal', 'artist-profile-image', 'artist_550e8400-e29b-41d4-a716-446655440007.jpg', 'image/jpeg');

-- =====================================================
-- SAMPLE ALBUMS
-- =====================================================

-- Linkin Park Albums
INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440000', 'Hybrid Theory', '2000-10-24', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440000.jpg', 'image/jpeg');

INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440001', 'Meteora', '2003-03-25', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440001.jpg', 'image/jpeg');

INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440002', 'Minutes to Midnight', '2007-05-14', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440002.jpg', 'image/jpeg');

-- Breaking Benjamin Albums
INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440010', 'Phobia', '2006-08-08', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440010.jpg', 'image/jpeg');

INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440011', 'Dear Agony', '2009-09-29', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440011.jpg', 'image/jpeg');

-- Three Days Grace Albums
INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440020', 'One-X', '2006-06-13', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440020.jpg', 'image/jpeg');

INSERT INTO album (id, title, release_date, cover_bucket, cover_object_key, cover_content_type)
VALUES ('660e8400-e29b-41d4-a716-446655440021', 'Life Starts Now', '2009-09-22', 'album-cover', 'album_660e8400-e29b-41d4-a716-446655440021.jpg', 'image/jpeg');

-- =====================================================
-- ARTIST-ALBUM RELATIONSHIPS
-- =====================================================

-- Linkin Park Albums
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440000');
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440001');
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440002');

-- Breaking Benjamin Albums
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440010');
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440011');

-- Three Days Grace Albums
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440020');
INSERT INTO album_artists (artist_id, album_id) VALUES ('550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440021');
