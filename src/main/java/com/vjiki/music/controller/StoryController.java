package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.StoryResponse;
import com.vjiki.music.service.StoryService;

/**
 * Instagram-style story controller
 * Provides endpoints for managing stories
 */
@RestController
@RequestMapping("/api/v1/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    /**
     * Get all active stories for a user in Instagram-style format
     * Returns stories that are active, not deleted, and not expired
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StoryResponse>> getStoriesByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(storyService.getStoriesByUserId(userId));
    }
}
