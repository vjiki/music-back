package com.vjiki.music.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.vjiki.music.entity.PlaylistSong;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, UUID> {

    @Query("SELECT ps FROM PlaylistSong ps LEFT JOIN FETCH ps.song LEFT JOIN FETCH ps.playlist WHERE ps.playlist.id = :playlistId AND ps.song.active = true ORDER BY ps.position ASC, ps.addedAt ASC")
    List<PlaylistSong> findByPlaylistId(@Param("playlistId") UUID playlistId);

    @Query("SELECT ps FROM PlaylistSong ps LEFT JOIN FETCH ps.song WHERE ps.playlist.id = :playlistId AND ps.song.active = true ORDER BY ps.position ASC, ps.addedAt ASC")
    List<PlaylistSong> findByPlaylistIdWithSong(@Param("playlistId") UUID playlistId);

    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
    Optional<PlaylistSong> findByPlaylistIdAndSongId(@Param("playlistId") UUID playlistId, @Param("songId") UUID songId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
    void deleteByPlaylistIdAndSongId(@Param("playlistId") UUID playlistId, @Param("songId") UUID songId);

    @Modifying
    @Query(value = """
            WITH playlist_id AS (
                SELECT id FROM music.playlists
                WHERE user_id = :userId AND name = :playlistName
                LIMIT 1
            ),
            max_position AS (
                SELECT COALESCE(MAX(position), -1) + 1 AS next_position
                FROM music.playlist_songs
                WHERE playlist_id = (SELECT id FROM playlist_id)
            )
            INSERT INTO music.playlist_songs (playlist_id, song_id, position, added_by, created_at, modified_at)
            SELECT
                (SELECT id FROM playlist_id),
                :songId,
                (SELECT next_position FROM max_position),
                :userId,
                NOW(),
                NOW()
            WHERE EXISTS (SELECT 1 FROM playlist_id)
              AND NOT EXISTS (
                  SELECT 1 FROM music.playlist_songs
                  WHERE playlist_id = (SELECT id FROM playlist_id) AND song_id = :songId
              )
            """, nativeQuery = true)
    void addSongToPlaylistIfNotExists(
            @Param("userId") UUID userId,
            @Param("songId") UUID songId,
            @Param("playlistName") String playlistName);
}
