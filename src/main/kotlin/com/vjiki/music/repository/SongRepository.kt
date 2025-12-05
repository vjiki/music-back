package com.vjiki.music.repository

import com.vjiki.music.entity.Song
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SongRepository : JpaRepository<Song, UUID>

