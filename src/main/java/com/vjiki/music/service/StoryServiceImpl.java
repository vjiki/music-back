package com.vjiki.music.service;

import com.vjiki.music.dto.StoryResponse;
import com.vjiki.music.entity.Story;
import com.vjiki.music.mapper.StoryMapper;
import com.vjiki.music.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final StoryMapper storyMapper;

    public StoryServiceImpl(StoryRepository storyRepository, StoryMapper storyMapper) {
        this.storyRepository = storyRepository;
        this.storyMapper = storyMapper;
    }

    @Override
    public List<StoryResponse> getStoriesByUserId(UUID userId) {
        OffsetDateTime now = OffsetDateTime.now();
        List<Story> stories = storyRepository.findActiveStoriesByUserId(userId, now);
        
        return stories.stream()
                .map(storyMapper::toResponse)
                .toList();
    }
}

