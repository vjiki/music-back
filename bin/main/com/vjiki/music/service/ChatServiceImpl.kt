package com.vjiki.music.service

import com.vjiki.music.dto.ChatResponse
import com.vjiki.music.dto.CreateChatRequest
import com.vjiki.music.mapper.ChatMapper.toResponse
import com.vjiki.music.repository.ChatParticipantRepository
import com.vjiki.music.repository.ChatRepository
import com.vjiki.music.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val userRepository: UserRepository
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

    override fun getChatById(chatId: UUID): ChatResponse {
        val chat = chatRepository.findByIdWithParticipants(chatId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found") }
        
        return chat.toResponse()
    }

    @Transactional
    override fun createChat(request: CreateChatRequest): ChatResponse {
        // Validate participants exist
        request.participantIds.forEach { userId ->
            if (!userRepository.existsById(userId)) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: $userId")
            }
        }

        // Validate owner exists if provided
        if (request.ownerId != null && !userRepository.existsById(request.ownerId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found")
        }

        // Generate UUID for the new chat
        val chatId = UUID.randomUUID()

        // Insert chat using native query
        chatRepository.insertChat(
            id = chatId,
            type = request.type,
            title = request.title,
            description = request.description,
            avatarUrl = request.avatarUrl,
            ownerId = request.ownerId,
            isEncrypted = request.isEncrypted
        )

        // Add participants
        request.participantIds.forEach { userId ->
            chatParticipantRepository.insertParticipant(
                chatId = chatId,
                userId = userId,
                role = if (userId == request.ownerId) "OWNER" else "MEMBER"
            )
        }

        // Fetch with participants for response
        val chatWithParticipants = chatRepository.findByIdWithParticipants(chatId)
            .orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve saved chat") }

        return chatWithParticipants.toResponse()
    }

    @Transactional
    override fun deleteChat(chatId: UUID) {
        // Validate chat exists
        if (!chatRepository.existsById(chatId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found")
        }

        // Delete chat (cascade will handle participants and messages)
        chatRepository.deleteChat(chatId)
    }
}

