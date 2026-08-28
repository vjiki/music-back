package com.vjiki.music.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    @Query("""
            SELECT a
            FROM Artist a
            WHERE a.isBand = true
            ORDER BY a.popularity DESC, a.sortName ASC
            """)
    List<Artist> findBands(Pageable pageable);

    @Query("""
            SELECT a
            FROM Artist a
            WHERE a.isBand = true
              AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(a.sortName) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY a.createdAt DESC, a.id DESC
            """)
    List<Artist> findBandsPageFirst(
            @Param("q") String q,
            Pageable pageable);

    @Query("""
            SELECT a
            FROM Artist a
            WHERE a.isBand = true
              AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(a.sortName) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (
                a.createdAt < :cursorCreatedAt
                OR (a.createdAt = :cursorCreatedAt AND a.id < :cursorId)
              )
            ORDER BY a.createdAt DESC, a.id DESC
            """)
    List<Artist> findBandsPageAfter(
            @Param("q") String q,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
}
