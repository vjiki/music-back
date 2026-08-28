package com.vjiki.music.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    @Query("SELECT s FROM Song s WHERE s.active = true AND s.type = 'SONG' ORDER BY s.createdAt DESC")
    List<Song> findAllActive();

    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.createdAt DESC")
    List<Song> findAllShorts();

    @Query(value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND s.type = 'SONG'
            ORDER BY s.created_at DESC, s.id DESC
            """, nativeQuery = true)
    List<Song> findActiveSongsPageFirst(Pageable pageable);

    @Query(value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND s.type = 'SONG'
              AND (
                s.created_at < :cursorCreatedAt
                OR (s.created_at = :cursorCreatedAt AND s.id < :cursorId)
              )
            ORDER BY s.created_at DESC, s.id DESC
            """, nativeQuery = true)
    List<Song> findActiveSongsPageAfter(
            @Param("cursorCreatedAt") OffsetDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    @Query(value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
            ORDER BY s.created_at DESC, s.id DESC
            """, nativeQuery = true)
    List<Song> findActiveItemsPageFirst(Pageable pageable);

    @Query(value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND (
                s.created_at < :cursorCreatedAt
                OR (s.created_at = :cursorCreatedAt AND s.id < :cursorId)
              )
            ORDER BY s.created_at DESC, s.id DESC
            """, nativeQuery = true)
    List<Song> findActiveItemsPageAfter(
            @Param("cursorCreatedAt") OffsetDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    // =========================
    // Search (SONG only)
    // Matches: title OR artists (jsonb array) OR tag.name via track_tag
    // Cursor-based pagination: created_at DESC, id DESC
    // =========================

    @Query(value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND s.type = 'SONG'
              AND (
                s.title ILIKE ('%' || :q || '%')
                OR EXISTS (
                  SELECT 1
                  FROM jsonb_array_elements_text(s.artists->'default') a
                  WHERE a ILIKE ('%' || :q || '%')
                )
                OR EXISTS (
                  SELECT 1
                  FROM music.track_tag tt
                  JOIN music.tag t ON t.id = tt.tag_id
                  WHERE tt.track_id = s.id
                    AND t.name ILIKE ('%' || :q || '%')
                )
              )
            ORDER BY s.created_at DESC, s.id DESC
            """, nativeQuery = true)
    List<Song> searchSongsFirst(
            @Param("q") String q,
            Pageable pageable);

    @Query(value = """
            SELECT *
            FROM music.songs s
            WHERE s.active = true
              AND s.type = 'SONG'
              AND (
                s.title ILIKE ('%' || :q || '%')
                OR EXISTS (
                  SELECT 1
                  FROM jsonb_array_elements_text(s.artists->'default') a
                  WHERE a ILIKE ('%' || :q || '%')
                )
                OR EXISTS (
                  SELECT 1
                  FROM music.track_tag tt
                  JOIN music.tag t ON t.id = tt.tag_id
                  WHERE tt.track_id = s.id
                    AND t.name ILIKE ('%' || :q || '%')
                )
              )
              AND (
                s.created_at < :cursorCreatedAt
                OR (s.created_at = :cursorCreatedAt AND s.id < :cursorId)
              )
            ORDER BY s.created_at DESC, s.id DESC
            """, nativeQuery = true)
    List<Song> searchSongsAfter(
            @Param("q") String q,
            @Param("cursorCreatedAt") OffsetDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
}
