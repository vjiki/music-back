package com.vjiki.music.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vjiki.music.dto.StoryResponse;
import com.vjiki.music.mapper.StoryMapper;
import com.vjiki.music.repository.StoryRepository;

@Service
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;

    public StoryServiceImpl(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    public List<StoryResponse> getStoriesByUserId(UUID userId) {
        OffsetDateTime now = OffsetDateTime.now();
        return storyRepository.findActiveStoriesByUserId(userId, now).stream()
                .map(StoryMapper::toResponse)
                .toList();
    }
}
