package com.vjiki.music.service

import com.vjiki.music.dto.StoryResponse
import com.vjiki.music.mapper.StoryMapper.toResponse
import com.vjiki.music.repository.StoryRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class StoryServiceImpl(
    private val storyRepository: StoryRepository
) : StoryService {

    override fun getStoriesByUserId(userId: UUID): List<StoryResponse> {
        val now = OffsetDateTime.now()
        return storyRepository.findActiveStoriesByUserId(userId, now)
            .map { it.toResponse() }
    }
}

