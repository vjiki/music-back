package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.dto.CreateChatRequest;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.mapper.ChatMapper;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import com.vjiki.music.repository.UserRepository;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    public ChatServiceImpl(ChatRepository chatRepository,
                           ChatParticipantRepository chatParticipantRepository,
                           UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ChatResponse> getChatsByUserId(UUID userId) {
        List<Chat> chats = chatRepository.findChatsByUserId(userId);

        List<ChatResponse> responses = new ArrayList<>(chats.size());
        for (Chat chat : chats) {
            List<ChatParticipant> participants = chatParticipantRepository.findByChatId(chat.getId());
            responses.add(ChatMapper.toResponse(chat, participants));
        }
        return responses;
    }

    @Override
    public ChatResponse getChatById(UUID chatId) {
        Chat chat = chatRepository.findByIdWithParticipants(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));

        return ChatMapper.toResponse(chat);
    }

    @Override
    @Transactional
    public ChatResponse createChat(CreateChatRequest request) {
        List<UUID> participantIds = request.getParticipantIds() == null
                ? List.of()
                : request.getParticipantIds();

        // Validate participants exist
        for (UUID userId : participantIds) {
            if (!userRepository.existsById(userId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
            }
        }

        // Validate owner exists if provided
        if (request.getOwnerId() != null && !userRepository.existsById(request.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
        }

        UUID chatId = UUID.randomUUID();

        chatRepository.insertChat(
                chatId,
                request.getType(),
                request.getTitle(),
                request.getDescription(),
                request.getAvatarUrl(),
                request.getOwnerId(),
                request.getIsEncrypted() != null && request.getIsEncrypted());

        for (UUID userId : participantIds) {
            String role = userId.equals(request.getOwnerId()) ? "OWNER" : "MEMBER";
            chatParticipantRepository.insertParticipant(chatId, userId, role);
        }

        Chat chatWithParticipants = chatRepository.findByIdWithParticipants(chatId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve saved chat"));

        return ChatMapper.toResponse(chatWithParticipants);
    }

    @Override
    @Transactional
    public void deleteChat(UUID chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found");
        }

        // Cascade handles participants and messages
        chatRepository.deleteChat(chatId);
    }
}
