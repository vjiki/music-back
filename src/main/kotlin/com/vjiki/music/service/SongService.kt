package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.pagination.CursorPageResponse
import java.util.UUID

interface SongService {
    fun getSongs(userId: UUID): List<SongResponse>

    fun getSongsPage(userId: UUID, limit: Int, cursor: String?): CursorPageResponse<SongResponse>
}

