package com.vjiki.music.repository

import com.vjiki.music.entity.Playlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlaylistRepository : JpaRepository<Playlist, UUID> {

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    fun findByUserId(@Param("userId") userId: UUID): List<Playlist>

    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.user WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    fun findByUserIdWithUser(@Param("userId") userId: UUID): List<Playlist>

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.type = :type")
    fun findByUserIdAndType(@Param("userId") userId: UUID, @Param("type") type: String): Optional<Playlist>

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.name = :name")
    fun findByUserIdAndName(@Param("userId") userId: UUID, @Param("name") name: String): Optional<Playlist>

    @Modifying
    @Query(
        value = """
            INSERT INTO music.playlists (user_id, name, description, cover_url, type, is_public, created_by, modified_by)
            SELECT :userId, :name, :description, :coverUrl, :type, :isPublic, :createdBy, :modifiedBy
            WHERE NOT EXISTS (
                SELECT 1
                FROM music.playlists p
                WHERE p.user_id = :userId
                  AND p.name = :name
            )
        """,
        nativeQuery = true
    )
    fun insertPlaylistIfMissing(
        @Param("userId") userId: UUID,
        @Param("name") name: String,
        @Param("description") description: String?,
        @Param("coverUrl") coverUrl: String?,
        @Param("type") type: String,
        @Param("isPublic") isPublic: Boolean,
        @Param("createdBy") createdBy: String = "system",
        @Param("modifiedBy") modifiedBy: String = "system"
    ): Int
}

