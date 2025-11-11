package com.vjiki.music.service;

import com.vjiki.music.dto.ChatResponse;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    List<ChatResponse> getChatsByUserId(UUID userId);
}

