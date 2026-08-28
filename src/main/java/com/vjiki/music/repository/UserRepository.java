package com.vjiki.music.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.vjiki.music.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    interface RegisterInfoProjection {
        UUID getId();

        String getProvider();

        String getProviderId();

        String getPasswordHash();
    }

    @Query(value = """
            SELECT u.id                         AS id,
                   u.provider                   AS provider,
                   u.provider_id                AS providerId,
                   u.password_hash              AS passwordHash
            FROM music.users u
            WHERE u.email = :email
            LIMIT 1
            """, nativeQuery = true)
    RegisterInfoProjection findRegisterInfoByEmailNative(@Param("email") String email);

    /**
     * Creates a user if missing, otherwise returns existing id (idempotent).
     * Uses ON CONFLICT(email) to handle races.
     */
    @Query(value = """
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
                created_by,
                modified_by
            ) VALUES (
                :email,
                :passwordHash,
                :provider,
                :providerId,
                :nickname,
                :avatarUrl,
                'USER',
                TRUE,
                FALSE,
                'system',
                'system'
            )
            ON CONFLICT (email) DO UPDATE
                SET email = EXCLUDED.email
            RETURNING id
            """, nativeQuery = true)
    UUID upsertUserReturnId(
            @Param("email") String email,
            @Param("passwordHash") String passwordHash,
            @Param("provider") String provider,
            @Param("providerId") String providerId,
            @Param("nickname") String nickname,
            @Param("avatarUrl") String avatarUrl);

    /**
     * If this is a LOCAL user and password_hash is currently NULL, set it.
     * Returns number of updated rows (0 or 1).
     */
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE music.users
            SET password_hash = :passwordHash,
                modified_by = 'system',
                modified_at = now(),
                version = version + 1
            WHERE id = :userId
              AND provider = 'LOCAL'
              AND password_hash IS NULL
            """, nativeQuery = true)
    int setPasswordIfMissing(
            @Param("userId") UUID userId,
            @Param("passwordHash") String passwordHash);
}
