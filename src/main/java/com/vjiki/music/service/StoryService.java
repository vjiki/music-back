package com.vjiki.music.service;

import com.vjiki.music.dto.StoryResponse;

import java.util.List;
import java.util.UUID;

public interface StoryService {
    List<StoryResponse> getStoriesByUserId(UUID userId);
}

