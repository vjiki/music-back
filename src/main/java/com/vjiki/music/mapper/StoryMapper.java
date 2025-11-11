package com.vjiki.music.mapper;

import com.vjiki.music.dto.StoryResponse;
import com.vjiki.music.entity.Story;

public interface StoryMapper {
    StoryResponse toResponse(Story story);
}

