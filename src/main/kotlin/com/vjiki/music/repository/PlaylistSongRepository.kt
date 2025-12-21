package com.vjiki.music.repository

import com.vjiki.music.entity.PlaylistSong
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface PlaylistSongRepository : JpaRepository<PlaylistSong, UUID> {

    @Query("SELECT ps FROM PlaylistSong ps LEFT JOIN FETCH ps.song LEFT JOIN FETCH ps.playlist WHERE ps.playlist.id = :playlistId AND ps.song.active = true ORDER BY ps.position ASC, ps.addedAt ASC")
    fun findByPlaylistId(@Param("playlistId") playlistId: UUID): List<PlaylistSong>

    @Query("SELECT ps FROM PlaylistSong ps LEFT JOIN FETCH ps.song WHERE ps.playlist.id = :playlistId AND ps.song.active = true ORDER BY ps.position ASC, ps.addedAt ASC")
    fun findByPlaylistIdWithSong(@Param("playlistId") playlistId: UUID): List<PlaylistSong>

    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
    fun findByPlaylistIdAndSongId(@Param("playlistId") playlistId: UUID, @Param("songId") songId: UUID): Optional<PlaylistSong>

    @Modifying
    @Transactional
    @Query("DELETE FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
    fun deleteByPlaylistIdAndSongId(@Param("playlistId") playlistId: UUID, @Param("songId") songId: UUID)
}

