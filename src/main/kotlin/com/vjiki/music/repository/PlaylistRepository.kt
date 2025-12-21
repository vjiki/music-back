package com.vjiki.music.repository

import com.vjiki.music.entity.Playlist
import org.springframework.data.jpa.repository.JpaRepository
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
}

