-- Grant permissions to music user for music schema
-- Run this as a superuser (e.g., avnadmin) in DataGrip or psql

-- Grant usage on schema
GRANT USAGE ON SCHEMA music TO music;

-- Grant all privileges on all tables in music schema
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA music TO music;

-- Grant privileges on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA music GRANT ALL ON TABLES TO music;

-- Grant privileges on sequences (for UUID generation)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA music TO music;
ALTER DEFAULT PRIVILEGES IN SCHEMA music GRANT ALL ON SEQUENCES TO music;

