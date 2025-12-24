package com.vjiki.music.service

import com.vjiki.music.dto.ShortResponse
import java.util.UUID

interface ShortService {
    fun getShorts(userId: UUID): List<ShortResponse>
}

