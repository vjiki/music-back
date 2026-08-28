package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.StoryResponse;

public interface StoryService {

    List<StoryResponse> getStoriesByUserId(UUID userId);
}
