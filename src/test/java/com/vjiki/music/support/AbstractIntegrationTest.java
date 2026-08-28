package com.vjiki.music.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.vjiki.music.config.TestContainersConfig;

/**
 * Shared wiring for every integration test.
 *
 * <p>All integration tests extend this class so that context-affecting annotations stay
 * identical and Spring can reuse cached contexts. That matters here beyond speed: the tests
 * share a single PostgreSQL container, so a context applying its own schema generation could
 * drop tables out from under another context that is still alive.
 *
 * <p>{@code ddl-auto=update} is used rather than {@code create-drop} for that reason - it
 * never drops existing tables. Per-test isolation comes from {@code @Transactional} rollback.
 */
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        // application.properties pins ssl=true/sslmode=require for the Aiven database;
        // the test container speaks plaintext, so the pool would never hand out a connection.
        "spring.datasource.hikari.data-source-properties.ssl=false",
        "spring.datasource.hikari.data-source-properties.sslmode=disable",
        // Production caps the pool at 2 to respect Aiven's connection limit; tests need slack.
        "spring.datasource.hikari.maximum-pool-size=5",
        // Production runs migrations by hand (ddl-auto=none); tests need the schema built.
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.jpa.properties.hibernate.default_schema=music",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.format_sql=false",
        // Keep Firebase out of the context so the missing service-account env vars don't fail startup.
        "firebase.enabled=false",
        "auth.local.enabled=true",
        "security.permit-all=true",
        "security.require-https=false"
})
public abstract class AbstractIntegrationTest {

    /**
     * The production schema gives surrogate keys and audit columns database-side defaults, and
     * several repository methods depend on that - {@code UserRepository.upsertUserReturnId},
     * {@code PlaylistRepository.insertPlaylistIfMissing}, {@code LikeRepository.insertLike} and
     * {@code DislikeRepository.insertDislike} all issue native INSERTs that omit those columns.
     * Hibernate generates the columns {@code not null} with no default, so those statements would
     * fail. Re-adding the defaults keeps the tests exercising the real native queries.
     */
    private static final List<String> DATABASE_SIDE_DEFAULTS = List.of(
            "ALTER TABLE music.users ALTER COLUMN id SET DEFAULT gen_random_uuid()",
            "ALTER TABLE music.users ALTER COLUMN created_at SET DEFAULT now()",
            "ALTER TABLE music.users ALTER COLUMN modified_at SET DEFAULT now()",
            "ALTER TABLE music.users ALTER COLUMN version SET DEFAULT 0",
            "ALTER TABLE music.playlists ALTER COLUMN id SET DEFAULT gen_random_uuid()",
            "ALTER TABLE music.playlists ALTER COLUMN created_at SET DEFAULT now()",
            "ALTER TABLE music.playlists ALTER COLUMN modified_at SET DEFAULT now()",
            "ALTER TABLE music.playlists ALTER COLUMN version SET DEFAULT 0",
            "ALTER TABLE music.playlist_songs ALTER COLUMN id SET DEFAULT gen_random_uuid()",
            "ALTER TABLE music.playlist_songs ALTER COLUMN added_at SET DEFAULT now()",
            "ALTER TABLE music.likes ALTER COLUMN id SET DEFAULT gen_random_uuid()",
            "ALTER TABLE music.likes ALTER COLUMN created_at SET DEFAULT now()",
            "ALTER TABLE music.dislikes ALTER COLUMN id SET DEFAULT gen_random_uuid()",
            "ALTER TABLE music.dislikes ALTER COLUMN created_at SET DEFAULT now()");

    private static volatile boolean defaultsApplied;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> TestContainersConfig.POSTGRES_CONTAINER.getJdbcUrl());
        registry.add("spring.datasource.username", () -> TestContainersConfig.POSTGRES_CONTAINER.getUsername());
        registry.add("spring.datasource.password", () -> TestContainersConfig.POSTGRES_CONTAINER.getPassword());
    }

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void applyDatabaseSideDefaults() throws SQLException {
        if (defaultsApplied) {
            return;
        }
        // Uses its own connection so the DDL survives the rollback of the test transaction.
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (String ddl : DATABASE_SIDE_DEFAULTS) {
                statement.execute(ddl);
            }
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        }
        defaultsApplied = true;
    }
}
