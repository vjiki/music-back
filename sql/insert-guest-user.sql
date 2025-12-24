-- Insert guest user into music.users table
-- Guest user without password (password_hash is NULL)

INSERT INTO music.users (
    email,
    password_hash,
    provider,
    provider_id,
    nickname,
    avatar_url,
    access_level,
    is_active,
    is_verified,
    last_login_at,
    last_login_ip,
    created_by,
    modified_by,
    version
) VALUES (
    'guest@example.com',
    NULL,
    'LOCAL',
    NULL,
    'Guest User',
    'https://api.dicebear.com/7.x/adventurer/jpg?seed=guest',
    'USER',
    true,
    false,
    NULL,
    NULL,
    'system',
    'system',
    0
);

