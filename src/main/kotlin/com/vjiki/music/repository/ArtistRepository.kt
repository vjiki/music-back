package com.vjiki.music.repository

import com.vjiki.music.entity.Artist
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ArtistRepository : JpaRepository<Artist, UUID> {

    @Query(
        """
        SELECT a
        FROM Artist a
        WHERE a.isBand = true
        ORDER BY a.popularity DESC, a.sortName ASC
        """
    )
    fun findBands(pageable: Pageable): List<Artist>

    @Query(
        """
        SELECT a
        FROM Artist a
        WHERE a.isBand = true
          AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(a.sortName) LIKE LOWER(CONCAT('%', :q, '%')))
        ORDER BY a.createdAt DESC, a.id DESC
        """
    )
    fun findBandsPageFirst(
        @Param("q") q: String,
        pageable: Pageable
    ): List<Artist>

    @Query(
        """
        SELECT a
        FROM Artist a
        WHERE a.isBand = true
          AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(a.sortName) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (
            a.createdAt < :cursorCreatedAt
            OR (a.createdAt = :cursorCreatedAt AND a.id < :cursorId)
          )
        ORDER BY a.createdAt DESC, a.id DESC
        """
    )
    fun findBandsPageAfter(
        @Param("q") q: String,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime,
        @Param("cursorId") cursorId: UUID,
        pageable: Pageable
    ): List<Artist>
}


