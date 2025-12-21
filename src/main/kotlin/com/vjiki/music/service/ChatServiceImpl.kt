package com.vjiki.music.service

import com.vjiki.music.dto.ChatResponse
import com.vjiki.music.entity.Chat
import com.vjiki.music.mapper.ChatMapper.toResponse
import com.vjiki.music.repository.ChatParticipantRepository
import com.vjiki.music.repository.ChatRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository
) : ChatService {

    override fun getChatsByUserId(userId: UUID): List<ChatResponse> {
        val chats = chatRepository.findChatsByUserId(userId)

        return chats.map { chat ->
            val participants = chatParticipantRepository.findByChatId(chat.id)
            // Create a new chat with loaded participants for mapping
            val chatWithParticipants = chat.copy(participants = participants.toMutableList())
            chatWithParticipants.toResponse()
        }
    }
}

