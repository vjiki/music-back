package com.vjiki.music.service;

import com.vjiki.music.dto.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    List<MessageResponse> getMessagesByChatId(UUID chatId, UUID userId1, UUID userId2);
}

