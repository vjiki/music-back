# music-back
backed for music

https://dashboard.render.com

https://console.aiven.io/account/a572a07566bf/project/vjiki/services




@Entity
@Table(name = "user_settings", schema = "music")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSettings {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String theme;               // LIGHT, DARK
    private String language;            // en, fr, etc.
    private Boolean notificationsEnabled;
    private Boolean explicitContentAllowed;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    private String createdBy;

    @UpdateTimestamp
    private OffsetDateTime modifiedAt;

    private String modifiedBy;

    @Version
    private Integer version;
}


@Entity
@Table(name = "users", schema = "music")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash; // nullable for OAuth users

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    private String providerId;
    private String nickname;
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessLevel accessLevel = AccessLevel.USER;

    private Boolean isActive = true;
    private Boolean isVerified = false;
    private OffsetDateTime lastLoginAt;
    private String lastLoginIp;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime modifiedAt;

    @Column(nullable = false)
    private String modifiedBy;

    @Version
    @Column(nullable = false)
    private Integer version;
}


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "songs", schema = "music")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue
    private UUID id;

    private String artist;
    private String audioUrl;
    private String coverUrl;
    private String title;
    private Boolean isFavourite;
    private Boolean isDisliked;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime modifiedAt;

    @Column(nullable = false)
    private String modifiedBy;

    @Version
    @Column(nullable = false)
    private Integer version;
}


SELECT song_id,
COUNT(*) FILTER (WHERE revoked_at IS NULL) AS active_likes
FROM music.likes
GROUP BY song_id;


SELECT s.*
FROM music.songs s
JOIN music.likes l ON l.song_id = s.id
WHERE l.user_id = :userId AND l.revoked_at IS NULL;


INSERT INTO music.playlists (user_id, name, type)
VALUES
(:userId, 'Liked Songs', 'LIKED_SONGS'),
(:userId, 'Disliked Songs', 'DISLIKED_SONGS');



-- Enable UUID support (if not yet enabled)
CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- Enable UUID support (if not yet enabled)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create schema (if missing)
CREATE SCHEMA IF NOT EXISTS music;

-- Create table
CREATE TABLE IF NOT EXISTS music.songs (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
artist VARCHAR(255) NOT NULL,
audio_url TEXT,
cover_url TEXT,
title VARCHAR(255) NOT NULL,
is_favourite BOOLEAN DEFAULT FALSE,
is_disliked BOOLEAN DEFAULT FALSE,

    -- audit fields
                                           created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           created_by VARCHAR(255) NOT NULL,
                                           modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           modified_by VARCHAR(255) NOT NULL,

    -- optimistic locking
                                           version INTEGER NOT NULL DEFAULT 0
);

-- ==========================================================
-- USERS
-- ==========================================================
CREATE TABLE IF NOT EXISTS music.users (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- === Identity & Authentication ===
                                           email              VARCHAR(255) UNIQUE NOT NULL,
                                           password_hash      TEXT,                     -- nullable for OAuth users
                                           provider           VARCHAR(50) NOT NULL DEFAULT 'LOCAL', -- LOCAL, GOOGLE, APPLE
                                           provider_id        VARCHAR(255),             -- external ID from Google/Apple

    -- === Profile Information ===
                                           nickname           VARCHAR(100) NOT NULL,
                                           avatar_url         TEXT,

    -- === Account & Access ===
                                           access_level       VARCHAR(50) NOT NULL DEFAULT 'USER', -- fallback role
                                           is_active          BOOLEAN NOT NULL DEFAULT TRUE,       -- can log in?
                                           is_verified        BOOLEAN NOT NULL DEFAULT FALSE,      -- email verified?
                                           last_login_at      TIMESTAMPTZ,                         -- last successful login
                                           last_login_ip      INET,                                -- optional audit

    -- === Auditing ===
                                           created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           created_by         VARCHAR(255) NOT NULL,
                                           modified_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           modified_by        VARCHAR(255) NOT NULL,

    -- === Optimistic Locking ===
                                           version            INTEGER NOT NULL DEFAULT 0
);

-- ==========================================================
-- USER ROLES
-- ==========================================================
CREATE TABLE IF NOT EXISTS music.user_roles (
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
role    VARCHAR(50) NOT NULL,
PRIMARY KEY (user_id, role)
);


-- ==========================================================
--  ALTER music.users  (Add missing columns for future use)
-- ==========================================================

ALTER TABLE music.users
ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS is_verified BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMPTZ,
ADD COLUMN IF NOT EXISTS last_login_ip INET,
ADD COLUMN IF NOT EXISTS access_level VARCHAR(50) NOT NULL DEFAULT 'USER';


-- ==========================================================
--  USER SETTINGS
-- ==========================================================

CREATE TABLE IF NOT EXISTS music.user_settings (
user_id UUID PRIMARY KEY
REFERENCES music.users(id)
ON DELETE CASCADE,

    -- === Preferences ===
                                                   theme VARCHAR(50) DEFAULT 'LIGHT',           -- LIGHT / DARK / SYSTEM
                                                   language VARCHAR(10) DEFAULT 'en',           -- ISO language code
                                                   notifications_enabled BOOLEAN DEFAULT TRUE,  -- email/push notifications
                                                   explicit_content_allowed BOOLEAN DEFAULT TRUE,

    -- === Auditing ===
                                                   created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                   created_by VARCHAR(255) NOT NULL DEFAULT 'system',
                                                   modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                   modified_by VARCHAR(255) NOT NULL DEFAULT 'system',

    -- === Optimistic Locking ===
                                                   version INTEGER NOT NULL DEFAULT 0
);


ALTER TABLE music.songs
ADD COLUMN IF NOT EXISTS uploaded_by UUID REFERENCES music.users(id) ON DELETE SET NULL,
ADD COLUMN IF NOT EXISTS likes_count BIGINT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS dislikes_count BIGINT NOT NULL DEFAULT 0;

ALTER TABLE music.songs
ADD COLUMN IF NOT EXISTS access_level VARCHAR(50) NOT NULL DEFAULT 'USER';

CREATE TABLE IF NOT EXISTS music.user_songs (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
song_id UUID NOT NULL REFERENCES music.songs(id) ON DELETE CASCADE,
added_at TIMESTAMPTZ NOT NULL DEFAULT now(),
source VARCHAR(50) DEFAULT 'UPLOAD', -- UPLOAD / SHARED / PURCHASED / IMPORTED
is_active BOOLEAN NOT NULL DEFAULT TRUE,

                                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                created_by VARCHAR(255) NOT NULL DEFAULT 'system',
                                                modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                modified_by VARCHAR(255) NOT NULL DEFAULT 'system',
                                                version INTEGER NOT NULL DEFAULT 0,

                                                UNIQUE (user_id, song_id)
);

CREATE TABLE IF NOT EXISTS music.playlists (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
name VARCHAR(255) NOT NULL,
description TEXT,
cover_url TEXT,
type VARCHAR(50) NOT NULL DEFAULT 'CUSTOM', -- CUSTOM / LIKED_SONGS / DISLIKED_SONGS
is_public BOOLEAN NOT NULL DEFAULT FALSE,

                                               created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                               created_by VARCHAR(255) NOT NULL DEFAULT 'system',
                                               modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                               modified_by VARCHAR(255) NOT NULL DEFAULT 'system',
                                               version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS music.playlist_songs (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
playlist_id UUID NOT NULL REFERENCES music.playlists(id) ON DELETE CASCADE,
song_id UUID NOT NULL REFERENCES music.songs(id) ON DELETE CASCADE,
position INTEGER NOT NULL DEFAULT 0,
added_at TIMESTAMPTZ NOT NULL DEFAULT now(),
added_by UUID REFERENCES music.users(id) ON DELETE SET NULL,

                                                    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                    modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                                    UNIQUE (playlist_id, song_id)
);


CREATE TABLE IF NOT EXISTS music.likes (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
song_id UUID NOT NULL REFERENCES music.songs(id) ON DELETE CASCADE,
created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- optional: if you want to track revokes or toggles
                                           revoked_at TIMESTAMPTZ,

                                           created_by VARCHAR(255) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS music.dislikes (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
song_id UUID NOT NULL REFERENCES music.songs(id) ON DELETE CASCADE,
created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
revoked_at TIMESTAMPTZ,
created_by VARCHAR(255) NOT NULL DEFAULT 'system'
);


CREATE TABLE IF NOT EXISTS music.chats (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
type VARCHAR(30) NOT NULL DEFAULT 'DIRECT',   -- DIRECT / GROUP / CHANNEL / SYSTEM
title VARCHAR(255),                           -- only for groups/channels
description TEXT,
avatar_url TEXT,
owner_id UUID REFERENCES music.users(id) ON DELETE SET NULL,

                                           is_encrypted BOOLEAN DEFAULT FALSE,
                                           is_archived BOOLEAN DEFAULT FALSE,
                                           is_muted BOOLEAN DEFAULT FALSE,
                                           created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS music.chat_participants (
chat_id UUID NOT NULL REFERENCES music.chats(id) ON DELETE CASCADE,
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
role VARCHAR(30) DEFAULT 'MEMBER',            -- MEMBER / ADMIN / OWNER
joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
is_muted BOOLEAN NOT NULL DEFAULT FALSE,
last_read_message_id UUID,                    -- for read receipts
PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE IF NOT EXISTS music.messages (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
chat_id UUID NOT NULL REFERENCES music.chats(id) ON DELETE CASCADE,
sender_id UUID REFERENCES music.users(id) ON DELETE SET NULL,
reply_to_id UUID REFERENCES music.messages(id) ON DELETE SET NULL,

                                              message_type VARCHAR(30) DEFAULT 'TEXT',      -- TEXT / IMAGE / VIDEO / SONG / SYSTEM
                                              content TEXT,
                                              song_id UUID REFERENCES music.songs(id) ON DELETE SET NULL,
                                              attachment_count INT DEFAULT 0,

                                              is_edited BOOLEAN NOT NULL DEFAULT FALSE,
                                              is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                              deleted_at TIMESTAMPTZ,
                                              created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                              updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                              version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS music.message_attachments (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
message_id UUID NOT NULL REFERENCES music.messages(id) ON DELETE CASCADE,
attachment_type VARCHAR(30) NOT NULL,        -- IMAGE / AUDIO / VIDEO / FILE
url TEXT NOT NULL,
thumbnail_url TEXT,
metadata JSONB,                              -- {duration, size, width, height}
created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS music.message_reactions (
message_id UUID NOT NULL REFERENCES music.messages(id) ON DELETE CASCADE,
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
emoji VARCHAR(20) NOT NULL,
created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
PRIMARY KEY (message_id, user_id, emoji)
);

CREATE TABLE IF NOT EXISTS music.message_reads (
message_id UUID NOT NULL REFERENCES music.messages(id) ON DELETE CASCADE,
user_id UUID NOT NULL REFERENCES music.users(id) ON DELETE CASCADE,
read_at TIMESTAMPTZ NOT NULL DEFAULT now(),
PRIMARY KEY (message_id, user_id)
);

CREATE TABLE IF NOT EXISTS music.message_edits (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
message_id UUID NOT NULL REFERENCES music.messages(id) ON DELETE CASCADE,
old_content TEXT,
new_content TEXT,
edited_at TIMESTAMPTZ NOT NULL DEFAULT now(),
edited_by UUID REFERENCES music.users(id)
);

-- Fast search by artist
CREATE INDEX idx_songs_artist ON music.songs (artist);

-- Fast search by title (partial match)
CREATE INDEX idx_songs_title ON music.songs (title);

-- Access level / favourite filtering queries
CREATE INDEX idx_songs_is_favourite ON music.songs (is_favourite);
CREATE INDEX idx_songs_is_disliked ON music.songs (is_disliked);

-- Recently added songs
CREATE INDEX idx_songs_created_at ON music.songs (created_at DESC);

-- Query all playlists of a user
CREATE INDEX idx_playlists_user_id ON music.playlists (user_id);

-- Query playlists by creation date
CREATE INDEX idx_playlists_created_at ON music.playlists (created_at DESC);

-- Optional: query by title for search
CREATE INDEX idx_playlists_title ON music.playlists (name);

-- Composite primary key
ALTER TABLE music.playlist_songs ADD PRIMARY KEY (playlist_id, song_id);

-- Find all songs in a playlist quickly
CREATE INDEX idx_playlist_songs_playlist_id ON music.playlist_songs (playlist_id);

-- Find all playlists that contain a song
CREATE INDEX idx_playlist_songs_song_id ON music.playlist_songs (song_id);

-- Optional: order songs by added_at
CREATE INDEX idx_playlist_songs_added_at ON music.playlist_songs (playlist_id, added_at);


-- Primary key
ALTER TABLE music.likes ADD PRIMARY KEY (user_id, song_id);
ALTER TABLE music.dislikes ADD PRIMARY KEY (user_id, song_id);

-- Find all likes for a song
CREATE INDEX idx_likes_song_id ON music.likes (song_id);
CREATE INDEX idx_dislikes_song_id ON music.dislikes (song_id);

-- Find all likes by a user
CREATE INDEX idx_likes_user_id ON music.likes (user_id);
CREATE INDEX idx_dislikes_user_id ON music.dislikes (user_id);

-- Recent likes/dislikes (optional)
CREATE INDEX idx_likes_created_at ON music.likes (created_at DESC);
CREATE INDEX idx_dislikes_created_at ON music.dislikes (created_at DESC);


-- Primary key
ALTER TABLE music.users ADD PRIMARY KEY (id);

-- Unique lookup by email
CREATE UNIQUE INDEX idx_users_email ON music.users (email);

-- Search by nickname
CREATE INDEX idx_users_nickname ON music.users (nickname);

-- Sort users by creation date (admin dashboards)
CREATE INDEX idx_users_created_at ON music.users (created_at DESC);

-- Filter by access_level
CREATE INDEX idx_users_access_level ON music.users (access_level);



-- Scotch songs
INSERT INTO music.songs (artist, audio_url, cover_url, title, is_favourite, is_disliked, created_by, modified_by)
VALUES
('Scotch', 'https://drive.google.com/uc?export=download&id=1Lyi-7lOLELU6sAjd3D9saShX6Hza56dK', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Шаг в темноту', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1kA19c1o1eK0ZFlCslmU6hmm-Tuot9hsA', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Реки слез', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1fyqbJobeUTZeaIP5b9ece5pUbVpAJG7O', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Цветы зла', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1eqERA3CsWVnzIHSeZKw2Y1tq14AOYizp', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Домино', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1qzlR0z8JCSQb8eTwYk7pODm8003DLj8p', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Звезды', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=19x6DQTF4lJ-T-Lq5s5poyVFLhN3dCFH-', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Падение', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1eQQRSq7hlk7YoMALUVfy6S9ivRhm5fA3', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Твой Мир', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1veW1fEVD-5wqd-_G8EC7ACVVC1D8jrV3', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Лето без тебя', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1hvzblDwo3pbHzTt7SKGSo4iBpBxBi9sq', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Горизонты', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1tfS0PN9_2jaP7xHs_eDu5hSF8zyOhu1z', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Холод стен', false, false, 'system', 'system'),
('Scotch', 'https://drive.google.com/uc?export=download&id=1zz2uJavwLDsgzGNWq-gE8nnrZ8_F3J2i', 'https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd', 'Не может длиться вечно', false, false, 'system', 'system');

-- 7раса songs
INSERT INTO music.songs (artist, audio_url, cover_url, title, is_favourite, is_disliked, created_by, modified_by)
VALUES
('7раса', 'https://drive.google.com/uc?export=download&id=1etxvzJXhdinj121-3fjNbq6bnh9PWIuL', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', '1й круг', false, false, 'system', 'system'),
('7раса', 'https://drive.google.com/uc?export=download&id=1vLi0-msdE7ywANn6TWlu1SplKEEI9EX4', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', 'В поисках рая', false, false, 'system', 'system'),
('7раса', 'https://drive.google.com/uc?export=download&id=1Mg9bfPmczARpO_BNReFb0lS--ygYQWKz', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', 'Вечное лето', false, false, 'system', 'system'),
('7раса', 'https://drive.google.com/uc?export=download&id=1nF-RzvuywP0vvBxQQw21WEORxbcf7l2S', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', 'Качели', false, false, 'system', 'system'),
('7раса', 'https://drive.google.com/uc?export=download&id=1rcCbVoe11he3IecVK5soYhdaoUylkAVc', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', 'Три цвета', false, false, 'system', 'system'),
('7раса', 'https://drive.google.com/uc?export=download&id=1UoZSGUrEmbKXmh1vz4viq2tp7wwhOVVR', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', 'Ты или я', false, false, 'system', 'system'),
('7раса', 'https://drive.google.com/uc?export=download&id=1b8mdnW9VsTk4AsIe01y8a5x3LFCqLkY7', 'https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-', 'Черная весна', false, false, 'system', 'system');

-- 1. Drop old columns
ALTER TABLE music.songs DROP COLUMN audio_url;
ALTER TABLE music.songs DROP COLUMN cover_url;
ALTER TABLE music.songs DROP COLUMN artists;

-- 2. Add new JSONB columns
ALTER TABLE music.songs
ADD COLUMN audio_urls jsonb NOT NULL DEFAULT '{}'::jsonb,
ADD COLUMN cover_urls jsonb NOT NULL DEFAULT '{}'::jsonb;

ALTER TABLE music.songs
ADD COLUMN artists jsonb NOT NULL DEFAULT '{}'::jsonb;


-- Insert script for all songs with JSONB maps

-- Scotch songs
INSERT INTO music.songs (artists, audio_urls, cover_urls, title, is_favourite, is_disliked, created_by, modified_by)
VALUES
('{"default":["Scotch"]}',
'{"default":"https://drive.google.com/uc?export=download&id=1Lyi-7lOLELU6sAjd3D9saShX6Hza56dK"}',
'{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
'Шаг в темноту', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1kA19c1o1eK0ZFlCslmU6hmm-Tuot9hsA"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Реки слез', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1fyqbJobeUTZeaIP5b9ece5pUbVpAJG7O"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Цветы зла', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1eqERA3CsWVnzIHSeZKw2Y1tq14AOYizp"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Домино', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1qzlR0z8JCSQb8eTwYk7pODm8003DLj8p"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Звезды', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=19x6DQTF4lJ-T-Lq5s5poyVFLhN3dCFH-"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Падение', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1eQQRSq7hlk7YoMALUVfy6S9ivRhm5fA3"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Твой Мир', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1veW1fEVD-5wqd-_G8EC7ACVVC1D8jrV3"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Лето без тебя', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1hvzblDwo3pbHzTt7SKGSo4iBpBxBi9sq"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Горизонты', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1tfS0PN9_2jaP7xHs_eDu5hSF8zyOhu1z"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Холод стен', false, false, 'system', 'system'),

    ('{"default":["Scotch"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1zz2uJavwLDsgzGNWq-gE8nnrZ8_F3J2i"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd"}',
     'Не может длиться вечно', false, false, 'system', 'system');

-- 7раса songs
INSERT INTO music.songs (artists, audio_urls, cover_urls, title, is_favourite, is_disliked, created_by, modified_by)
VALUES
('{"default":["7раса"]}',
'{"default":"https://drive.google.com/uc?export=download&id=1etxvzJXhdinj121-3fjNbq6bnh9PWIuL"}',
'{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
'1й круг', false, false, 'system', 'system'),

    ('{"default":["7раса"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1vLi0-msdE7ywANn6TWlu1SplKEEI9EX4"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
     'В поисках рая', false, false, 'system', 'system'),

    ('{"default":["7раса"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1Mg9bfPmczARpO_BNReFb0lS--ygYQWKz"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
     'Вечное лето', false, false, 'system', 'system'),

    ('{"default":["7раса"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1nF-RzvuywP0vvBxQQw21WEORxbcf7l2S"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
     'Качели', false, false, 'system', 'system'),

    ('{"default":["7раса"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1rcCbVoe11he3IecVK5soYhdaoUylkAVc"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
     'Три цвета', false, false, 'system', 'system'),

    ('{"default":["7раса"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1UoZSGUrEmbKXmh1vz4viq2tp7wwhOVVR"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
     'Ты или я', false, false, 'system', 'system'),

    ('{"default":["7раса"]}',
     '{"default":"https://drive.google.com/uc?export=download&id=1b8mdnW9VsTk4AsIe01y8a5x3LFCqLkY7"}',
     '{"default":"https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-"}',
     'Черная весна', false, false, 'system', 'system');


-- Example users
INSERT INTO music.users (
id, email, password_hash, provider, provider_id, nickname, avatar_url,
access_level, is_active, is_verified, last_login_at, last_login_ip,
created_at, created_by, modified_at, modified_by, version
)
VALUES
('11111111-1111-1111-1111-111111111111', 'alice@example.com', NULL, 'LOCAL', NULL, 'Alice', NULL, 'ADMIN', TRUE, TRUE, NULL, NULL, now(), 'system', now(), 'system', 0),
('22222222-2222-2222-2222-222222222222', 'bob@example.com', NULL, 'LOCAL', NULL, 'Bob', NULL, 'USER', TRUE, TRUE, NULL, NULL, now(), 'system', now(), 'system', 0),
('33333333-3333-3333-3333-333333333333', 'carol@example.com', NULL, 'LOCAL', NULL, 'Carol', NULL, 'USER', TRUE, FALSE, NULL, NULL, now(), 'system', now(), 'system', 0),
('44444444-4444-4444-4444-444444444444', 'dave@example.com', NULL, 'LOCAL', NULL, 'Dave', NULL, 'MODERATOR', TRUE, TRUE, NULL, NULL, now(), 'system', now(), 'system', 0);

-- Assign roles to users
INSERT INTO music.user_roles (user_id, role) VALUES
('11111111-1111-1111-1111-111111111111', 'ADMIN'),
('11111111-1111-1111-1111-111111111111', 'USER'),
('22222222-2222-2222-2222-222222222222', 'USER'),
('33333333-3333-3333-3333-333333333333', 'USER'),
('44444444-4444-4444-4444-444444444444', 'MODERATOR'),
('44444444-4444-4444-4444-444444444444', 'USER');

-- Example: assign uploaded_by for songs (UUIDs from users)
UPDATE music.songs
SET uploaded_by = '11111111-1111-1111-1111-111111111111'
WHERE title IN ('Шаг в темноту', 'Реки слез');

UPDATE music.songs
SET uploaded_by = '22222222-2222-2222-2222-222222222222'
WHERE title IN ('В поисках рая', '1й круг');

UPDATE music.songs
SET uploaded_by = '33333333-3333-3333-3333-333333333333'
WHERE title IN ('Цветы зла', 'Домино');

UPDATE music.songs
SET uploaded_by = '44444444-4444-4444-4444-444444444444'
WHERE title IN ('Три цвета', 'Ты или я');



