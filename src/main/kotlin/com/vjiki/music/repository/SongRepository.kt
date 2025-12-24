package com.vjiki.music.repository

import com.vjiki.music.entity.Song
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SongRepository : JpaRepository<Song, UUID> {
    
    @Query("SELECT s FROM Song s WHERE s.active = true AND s.type = 'SONG' ORDER BY s.createdAt DESC")
    fun findAllActive(): List<Song>
    
    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.createdAt DESC")
    fun findAllShorts(): List<Song>
}

