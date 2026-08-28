package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.ChatListItemResponse;

public interface ChatListService {

    List<ChatListItemResponse> getChatListForUser(UUID userId);
}
