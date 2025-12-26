package com.vjiki.music.repository

import com.vjiki.music.entity.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LikeRepository : JpaRepository<Like, UUID> {

    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.id = :userId AND l.song.id = :songId AND l.revokedAt IS NULL")
    fun existsByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") userId: UUID, @Param("songId") songId: UUID): Boolean

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.song.id = :songId AND l.revokedAt IS NULL")
    fun findByUserIdAndSongIdAndRevokedAtIsNull(@Param("userId") userId: UUID, @Param("songId") songId: UUID): Optional<Like>

    @Query("SELECT COUNT(l) FROM Like l WHERE l.song.id = :songId AND l.revokedAt IS NULL")
    fun countBySongIdAndRevokedAtIsNull(@Param("songId") songId: UUID): Long

    @Query(
        """
            SELECT l.song.id
            FROM Like l
            WHERE l.user.id = :userId
              AND l.song.id IN :songIds
              AND l.revokedAt IS NULL
        """
    )
    fun findActiveLikedSongIds(
        @Param("userId") userId: UUID,
        @Param("songIds") songIds: Collection<UUID>
    ): List<UUID>

    @Query(
        """
            SELECT l.song.id AS songId, COUNT(l) AS cnt
            FROM Like l
            WHERE l.song.id IN :songIds
              AND l.revokedAt IS NULL
            GROUP BY l.song.id
        """
    )
    fun countActiveLikesBySongIds(
        @Param("songIds") songIds: Collection<UUID>
    ): List<SongIdCountProjection>

    @Modifying
    @Query(
        value = "INSERT INTO music.likes (user_id, song_id, created_by) VALUES (:userId, :songId, :createdBy)",
        nativeQuery = true
    )
    fun insertLike(
        @Param("userId") userId: UUID,
        @Param("songId") songId: UUID,
        @Param("createdBy") createdBy: String = "system"
    )
}

