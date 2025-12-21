package com.vjiki.music.repository

import com.vjiki.music.entity.Dislike
import org.springframework.data.jpa.repository.JpaRepository
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
}

