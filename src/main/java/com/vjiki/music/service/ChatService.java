package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.dto.CreateChatRequest;

public interface ChatService {

    List<ChatResponse> getChatsByUserId(UUID userId);

    ChatResponse getChatById(UUID chatId);

    ChatResponse createChat(CreateChatRequest request);

    void deleteChat(UUID chatId);
}
