package com.vjiki.music.service

import com.vjiki.music.dto.StoryResponse
import java.util.UUID

interface StoryService {
    fun getStoriesByUserId(userId: UUID): List<StoryResponse>
}

