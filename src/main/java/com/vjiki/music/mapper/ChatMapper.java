package com.vjiki.music.mapper;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.entity.Chat;

public interface ChatMapper {
    ChatResponse toResponse(Chat chat);
}

