package com.vjiki.music.service

import com.vjiki.music.dto.ShortResponse
import com.vjiki.music.pagination.CursorPageResponse
import java.util.UUID

interface ShortService {
    fun getShorts(userId: UUID): List<ShortResponse>

    fun getShortsPage(userId: UUID, limit: Int, cursor: String?): CursorPageResponse<ShortResponse>
}

