package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse

interface SongService {
    fun getSongs(userId: String): List<SongResponse>
}

