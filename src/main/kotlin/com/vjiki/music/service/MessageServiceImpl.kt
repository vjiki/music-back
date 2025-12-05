package com.vjiki.music.service

import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.mapper.MessageMapper.toResponse
import com.vjiki.music.repository.MessageRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository
) : MessageService {

    override fun getMessagesByChatId(chatId: UUID, userId1: UUID, userId2: UUID): List<MessageResponse> {
        return messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2)
            .map { it.toResponse() }
    }
}

