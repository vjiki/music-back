package com.vjiki.music.repository

import com.vjiki.music.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>

    interface RegisterInfoProjection {
        val id: UUID
        val provider: String
        val passwordHash: String?
    }

    @Query(
        value = """
            SELECT u.id                         AS id,
                   u.provider                   AS provider,
                   u.password_hash              AS passwordHash
            FROM music.users u
            WHERE u.email = :email
            LIMIT 1
        """,
        nativeQuery = true
    )
    fun findRegisterInfoByEmailNative(@Param("email") email: String): RegisterInfoProjection?

    /**
     * Creates a LOCAL user if missing, otherwise returns existing id (idempotent).
     * Uses ON CONFLICT(email) to handle races.
     */
    @Query(
        value = """
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
                'LOCAL',
                NULL,
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
        """,
        nativeQuery = true
    )
    fun upsertLocalUserReturnId(
        @Param("email") email: String,
        @Param("passwordHash") passwordHash: String?,
        @Param("nickname") nickname: String,
        @Param("avatarUrl") avatarUrl: String?
    ): UUID

    /**
     * If this is a LOCAL user and password_hash is currently NULL, set it.
     * Returns number of updated rows (0 or 1).
     */
    @Modifying
    @Transactional
    @Query(
        value = """
            UPDATE music.users
            SET password_hash = :passwordHash,
                modified_by = 'system',
                modified_at = now(),
                version = version + 1
            WHERE id = :userId
              AND provider = 'LOCAL'
              AND password_hash IS NULL
        """,
        nativeQuery = true
    )
    fun setPasswordIfMissing(
        @Param("userId") userId: UUID,
        @Param("passwordHash") passwordHash: String
    ): Int
}

