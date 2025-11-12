package com.vjiki.music.repository;

import com.vjiki.music.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, UUID> {

    @Query("SELECT ps FROM PlaylistSong ps LEFT JOIN FETCH ps.song LEFT JOIN FETCH ps.playlist WHERE ps.playlist.id = :playlistId ORDER BY ps.position ASC, ps.addedAt ASC")
    List<PlaylistSong> findByPlaylistId(@Param("playlistId") UUID playlistId);

    @Query("SELECT ps FROM PlaylistSong ps LEFT JOIN FETCH ps.song WHERE ps.playlist.id = :playlistId ORDER BY ps.position ASC, ps.addedAt ASC")
    List<PlaylistSong> findByPlaylistIdWithSong(@Param("playlistId") UUID playlistId);

    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
    Optional<PlaylistSong> findByPlaylistIdAndSongId(@Param("playlistId") UUID playlistId, @Param("songId") UUID songId);

    @Modifying
    @Query("DELETE FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
    void deleteByPlaylistIdAndSongId(@Param("playlistId") UUID playlistId, @Param("songId") UUID songId);
}

