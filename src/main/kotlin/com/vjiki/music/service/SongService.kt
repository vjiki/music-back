package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse
import java.util.UUID

interface SongService {
    fun getSongs(userId: UUID): List<SongResponse>
}

