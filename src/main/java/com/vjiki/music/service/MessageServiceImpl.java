package com.vjiki.music.service;

import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.entity.Message;
import com.vjiki.music.mapper.MessageMapper;
import com.vjiki.music.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository,
                             MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public List<MessageResponse> getMessagesByChatId(UUID chatId, UUID userId1, UUID userId2) {
        List<Message> messages = messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2);
        
        return messages.stream()
                .map(messageMapper::toResponse)
                .toList();
    }
}

