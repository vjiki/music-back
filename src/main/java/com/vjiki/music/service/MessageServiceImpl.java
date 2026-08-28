package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vjiki.music.dto.CreateMessageRequest;
import com.vjiki.music.dto.MessageReactionRequest;
import com.vjiki.music.dto.MessageReactionResponse;
import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.entity.Message;
import com.vjiki.music.mapper.MessageMapper;
import com.vjiki.music.pagination.CreatedAtIdCursorCodec;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import com.vjiki.music.repository.MessageReactionRepository;
import com.vjiki.music.repository.MessageReadRepository;
import com.vjiki.music.repository.MessageRepository;
import com.vjiki.music.repository.UserRepository;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageReactionRepository messageReactionRepository;
    private final MessageReadRepository messageReadRepository;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    public MessageServiceImpl(MessageRepository messageRepository,
                              MessageReactionRepository messageReactionRepository,
                              MessageReadRepository messageReadRepository,
                              ChatRepository chatRepository,
                              ChatParticipantRepository chatParticipantRepository,
                              UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.messageReactionRepository = messageReactionRepository;
        this.messageReadRepository = messageReadRepository;
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<MessageResponse> getMessagesByChatId(UUID chatId, UUID userId1, UUID userId2) {
        return messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2).stream()
                .map(MessageMapper::toResponse)
                .toList();
    }

    @Override
    public CursorPageResponse<MessageResponse> getMessagesPage(UUID chatId, int limit, String cursor) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        CreatedAtIdCursorCodec.Cursor decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor);
        List<Message> messages = decoded == null
                ? messageRepository.findMessagesPageFirst(chatId, pageable)
                : messageRepository.findMessagesPageAfter(chatId, decoded.createdAt(), decoded.id(), pageable);

        if (messages.isEmpty()) {
            return new CursorPageResponse<>(List.of(), null, false);
        }

        boolean hasNext = messages.size() > safeLimit;
        List<Message> slice = hasNext ? messages.subList(0, safeLimit) : messages;

        // Native queries don't load relationships, so re-read the slice through JPA to get senders.
        List<UUID> messageIds = slice.stream().map(Message::getId).toList();
        Map<UUID, Message> messagesWithSenders = new HashMap<>();
        for (Message message : messageRepository.findAllById(messageIds)) {
            messagesWithSenders.put(message.getId(), message);
        }

        List<MessageResponse> items = new ArrayList<>(slice.size());
        for (Message message : slice) {
            Message withSender = messagesWithSenders.getOrDefault(message.getId(), message);
            items.add(MessageMapper.toResponse(withSender));
        }

        Message last = slice.getLast();
        String nextCursor = (hasNext && last.getCreatedAt() != null)
                ? CreatedAtIdCursorCodec.encode(last.getCreatedAt(), last.getId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    @Override
    @Transactional
    public MessageResponse createMessage(CreateMessageRequest request) {
        if (!chatRepository.existsById(request.getChatId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found");
        }

        if (chatParticipantRepository.findByChatIdAndUserId(request.getChatId(), request.getSenderId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a participant in this chat");
        }

        if (!userRepository.existsById(request.getSenderId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found");
        }

        if (request.getReplyToId() != null) {
            Message replyToMessage = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Reply-to message not found"));

            if (!replyToMessage.getChatId().equals(request.getChatId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Reply-to message does not belong to this chat");
            }
        }

        UUID messageId = UUID.randomUUID();

        messageRepository.insertMessage(
                messageId,
                request.getChatId(),
                request.getSenderId(),
                request.getReplyToId(),
                request.getMessageType(),
                request.getContent(),
                request.getSongId(),
                request.getAttachmentCount() == null ? 0 : request.getAttachmentCount());

        chatRepository.updateChatTimestamp(request.getChatId());

        Message messageWithSender = messageRepository.findByIdWithSender(messageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve saved message"));

        return MessageMapper.toResponse(messageWithSender);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if (Boolean.TRUE.equals(message.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is already deleted");
        }

        messageRepository.deleteMessage(messageId);
    }

    @Override
    public List<MessageReactionResponse> getMessageReactions(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }

        return messageReactionRepository.findByMessageId(messageId).stream()
                .map(reaction -> MessageReactionResponse.builder()
                        .messageId(reaction.getMessageId())
                        .userId(reaction.getUserId())
                        .emoji(reaction.getEmoji())
                        .createdAt(reaction.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void addReaction(MessageReactionRequest request) {
        if (!messageRepository.existsById(request.getMessageId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        messageReactionRepository.insertReaction(request.getMessageId(), request.getUserId(), request.getEmoji());
    }

    @Override
    @Transactional
    public void removeReaction(UUID messageId, UUID userId, String emoji) {
        if (!messageRepository.existsById(messageId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        messageReactionRepository.deleteReaction(messageId, userId, emoji);
    }

    @Override
    @Transactional
    public void markAsRead(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (chatParticipantRepository.findByChatIdAndUserId(message.getChatId(), userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a participant in this chat");
        }

        messageReadRepository.markAsRead(messageId, userId);

        chatParticipantRepository.updateLastReadMessageId(message.getChatId(), userId, messageId);
    }
}
