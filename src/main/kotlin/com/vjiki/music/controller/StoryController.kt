package com.vjiki.music.controller

import com.vjiki.music.dto.StoryResponse
import com.vjiki.music.service.StoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Instagram-style story controller
 * Provides endpoints for managing stories
 */
@RestController
@RequestMapping("/api/v1/stories")
class StoryController(
    private val storyService: StoryService
) {

    /**
     * Get all active stories for a user in Instagram-style format
     * Returns stories that are active, not deleted, and not expired
     */
    @GetMapping("/user/{userId}")
    fun getStoriesByUserId(@PathVariable userId: UUID): ResponseEntity<List<StoryResponse>> {
        val stories = storyService.getStoriesByUserId(userId)
        return ResponseEntity.ok(stories)
    }
}

