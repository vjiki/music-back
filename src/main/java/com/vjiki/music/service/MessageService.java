package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.CreateMessageRequest;
import com.vjiki.music.dto.MessageReactionRequest;
import com.vjiki.music.dto.MessageReactionResponse;
import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.pagination.CursorPageResponse;

public interface MessageService {

    List<MessageResponse> getMessagesByChatId(UUID chatId, UUID userId1, UUID userId2);

    CursorPageResponse<MessageResponse> getMessagesPage(UUID chatId, int limit, String cursor);

    MessageResponse createMessage(CreateMessageRequest request);

    void deleteMessage(UUID messageId);

    List<MessageReactionResponse> getMessageReactions(UUID messageId);

    void addReaction(MessageReactionRequest request);

    void removeReaction(UUID messageId, UUID userId, String emoji);

    void markAsRead(UUID messageId, UUID userId);
}
