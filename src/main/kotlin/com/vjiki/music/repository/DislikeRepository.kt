package com.vjiki.music.repository

import com.vjiki.music.entity.Dislike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DislikeRepository : JpaRepository<Dislike, UUID> {

    @Query("SELECT COUNT(d) > 0 FROM Dislike d WHERE d.user.id = :userId AND d.song.id = :songId AND d.revokedAt IS NULL")
    fun existsByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") userId: UUID, @Param("songId") songId: UUID): Boolean

    @Query("SELECT d FROM Dislike d WHERE d.user.id = :userId AND d.song.id = :songId AND d.revokedAt IS NULL")
    fun findByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") userId: UUID, @Param("songId") songId: UUID): Optional<Dislike>

    @Query("SELECT COUNT(d) FROM Dislike d WHERE d.song.id = :songId AND d.revokedAt IS NULL")
    fun countBySongIdAndRevokedAtIsNull(@Param("songId") songId: UUID): Long

    @Query(
        """
            SELECT d.song.id
            FROM Dislike d
            WHERE d.user.id = :userId
              AND d.song.id IN :songIds
              AND d.revokedAt IS NULL
        """
    )
    fun findActiveDislikedSongIds(
        @Param("userId") userId: UUID,
        @Param("songIds") songIds: Collection<UUID>
    ): List<UUID>

    @Query(
        """
            SELECT d.song.id AS songId, COUNT(d) AS cnt
            FROM Dislike d
            WHERE d.song.id IN :songIds
              AND d.revokedAt IS NULL
            GROUP BY d.song.id
        """
    )
    fun countActiveDislikesBySongIds(
        @Param("songIds") songIds: Collection<UUID>
    ): List<SongIdCountProjection>

    @Modifying
    @Query(
        value = "INSERT INTO music.dislikes (user_id, song_id, created_by) VALUES (:userId, :songId, :createdBy)",
        nativeQuery = true
    )
    fun insertDislike(
        @Param("userId") userId: UUID,
        @Param("songId") songId: UUID,
        @Param("createdBy") createdBy: String = "system"
    )
}

