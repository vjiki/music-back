package com.vjiki.music.repository

import com.vjiki.music.entity.Song
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface SongRepository : JpaRepository<Song, UUID> {
    
    @Query("SELECT s FROM Song s WHERE s.active = true AND s.type = 'SONG' ORDER BY s.createdAt DESC")
    fun findAllActive(): List<Song>
    
    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.createdAt DESC")
    fun findAllShorts(): List<Song>

    @Query(
        value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND s.type = 'SONG'
            ORDER BY s.created_at DESC, s.id DESC
        """,
        nativeQuery = true
    )
    fun findActiveSongsPageFirst(
        pageable: Pageable
    ): List<Song>

    @Query(
        value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND s.type = 'SONG'
              AND (
                s.created_at < :cursorCreatedAt
                OR (s.created_at = :cursorCreatedAt AND s.id < :cursorId)
              )
            ORDER BY s.created_at DESC, s.id DESC
        """,
        nativeQuery = true
    )
    fun findActiveSongsPageAfter(
        @Param("cursorCreatedAt") cursorCreatedAt: OffsetDateTime,
        @Param("cursorId") cursorId: UUID,
        pageable: Pageable
    ): List<Song>

    @Query(
        value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
            ORDER BY s.created_at DESC, s.id DESC
        """,
        nativeQuery = true
    )
    fun findActiveItemsPageFirst(
        pageable: Pageable
    ): List<Song>

    @Query(
        value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND (
                s.created_at < :cursorCreatedAt
                OR (s.created_at = :cursorCreatedAt AND s.id < :cursorId)
              )
            ORDER BY s.created_at DESC, s.id DESC
        """,
        nativeQuery = true
    )
    fun findActiveItemsPageAfter(
        @Param("cursorCreatedAt") cursorCreatedAt: OffsetDateTime,
        @Param("cursorId") cursorId: UUID,
        pageable: Pageable
    ): List<Song>
}

