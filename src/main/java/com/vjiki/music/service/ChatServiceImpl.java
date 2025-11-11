package com.vjiki.music.service;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.mapper.ChatMapper;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMapper chatMapper;

    public ChatServiceImpl(ChatRepository chatRepository,
                          ChatParticipantRepository chatParticipantRepository,
                          ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMapper = chatMapper;
    }

    @Override
    public List<ChatResponse> getChatsByUserId(UUID userId) {
        List<Chat> chats = chatRepository.findChatsByUserId(userId);
        
        return chats.stream()
                .map(chat -> {
                    // Load participants for each chat
                    List<ChatParticipant> participants = chatParticipantRepository.findByChatId(chat.getId());
                    chat.setParticipants(participants);
                    return chatMapper.toResponse(chat);
                })
                .toList();
    }
}

