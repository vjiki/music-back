package com.vjiki.music.service;

import com.vjiki.music.dto.ChatListItemResponse;

import java.util.List;
import java.util.UUID;

public interface ChatListService {
    List<ChatListItemResponse> getChatListForUser(UUID userId);
}

