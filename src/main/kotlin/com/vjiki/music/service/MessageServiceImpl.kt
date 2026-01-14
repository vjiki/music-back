package com.vjiki.music.service

import com.vjiki.music.dto.CreateMessageRequest
import com.vjiki.music.dto.MessageReactionRequest
import com.vjiki.music.dto.MessageReactionResponse
import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.mapper.MessageMapper.toResponse
import com.vjiki.music.pagination.CreatedAtIdCursorCodec
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.repository.ChatParticipantRepository
import com.vjiki.music.repository.ChatRepository
import com.vjiki.music.repository.MessageReadRepository
import com.vjiki.music.repository.MessageReactionRepository
import com.vjiki.music.repository.MessageRepository
import com.vjiki.music.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val messageReactionRepository: MessageReactionRepository,
    private val messageReadRepository: MessageReadRepository,
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val userRepository: UserRepository
) : MessageService {

    override fun getMessagesByChatId(chatId: UUID, userId1: UUID, userId2: UUID): List<MessageResponse> {
        return messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2)
            .map { it.toResponse() }
    }

    override fun getMessagesPage(chatId: UUID, limit: Int, cursor: String?): CursorPageResponse<MessageResponse> {
        val safeLimit = limit.coerceIn(1, 100)
        val pageable = PageRequest.of(0, safeLimit + 1)

        val decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor)
        val messages = if (decoded == null) {
            messageRepository.findMessagesPageFirst(chatId, pageable)
        } else {
            messageRepository.findMessagesPageAfter(
                chatId = chatId,
                cursorCreatedAt = decoded.createdAt,
                cursorId = decoded.id,
                pageable = pageable
            )
        }

        if (messages.isEmpty()) {
            return CursorPageResponse(
                items = emptyList(),
                nextCursor = null,
                hasNext = false
            )
        }

        val hasNext = messages.size > safeLimit
        val slice = if (hasNext) messages.take(safeLimit) else messages

        // Fetch senders for messages (native queries don't load relationships)
        val messageIds = slice.map { it.id }
        val messagesWithSenders = if (messageIds.isNotEmpty()) {
            messageRepository.findAllById(messageIds).associateBy { it.id }
        } else {
            emptyMap()
        }

        val items = slice.map { message ->
            val messageWithSender = messagesWithSenders[message.id] ?: message
            messageWithSender.toResponse()
        }

        val last = slice.lastOrNull()
        val nextCursor = if (hasNext && last?.createdAt != null) {
            CreatedAtIdCursorCodec.encode(last.createdAt, last.id)
        } else {
            null
        }

        return CursorPageResponse(
            items = items,
            nextCursor = nextCursor,
            hasNext = hasNext
        )
    }

    @Transactional
    override fun createMessage(request: CreateMessageRequest): MessageResponse {
        // Validate chat exists
        if (!chatRepository.existsById(request.chatId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found")
        }

        // Validate sender is participant
        val participant = chatParticipantRepository.findByChatIdAndUserId(request.chatId, request.senderId)
        if (participant.isEmpty) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a participant in this chat")
        }

        // Validate sender exists
        if (!userRepository.existsById(request.senderId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found")
        }

        // Validate replyTo message exists and belongs to same chat
        if (request.replyToId != null) {
            val replyToMessage = messageRepository.findById(request.replyToId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Reply-to message not found") }
            
            if (replyToMessage.chatId != request.chatId) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Reply-to message does not belong to this chat")
            }
        }

        // Generate UUID for the new message
        val messageId = UUID.randomUUID()

        // Insert message using native query
        messageRepository.insertMessage(
            id = messageId,
            chatId = request.chatId,
            senderId = request.senderId,
            replyToId = request.replyToId,
            messageType = request.messageType,
            content = request.content,
            songId = request.songId,
            attachmentCount = request.attachmentCount
        )

        // Update chat's updated_at timestamp
        chatRepository.updateChatTimestamp(request.chatId)

        // Fetch with sender relation for response
        val messageWithSender = messageRepository.findByIdWithSender(messageId)
            .orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve saved message") }

        return messageWithSender.toResponse()
    }

    @Transactional
    override fun deleteMessage(messageId: UUID) {
        // Validate message exists
        val message = messageRepository.findById(messageId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found") }

        if (message.isDeleted) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is already deleted")
        }

        // Soft delete the message
        messageRepository.deleteMessage(messageId)
    }

    override fun getMessageReactions(messageId: UUID): List<MessageReactionResponse> {
        // Validate message exists
        if (!messageRepository.existsById(messageId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")
        }

        val reactions = messageReactionRepository.findByMessageId(messageId)
        return reactions.map { reaction ->
            MessageReactionResponse(
                messageId = reaction.messageId,
                userId = reaction.userId,
                emoji = reaction.emoji,
                createdAt = reaction.createdAt
            )
        }
    }

    @Transactional
    override fun addReaction(request: MessageReactionRequest) {
        // Validate message exists
        if (!messageRepository.existsById(request.messageId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")
        }

        // Validate user exists
        if (!userRepository.existsById(request.userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        // Insert reaction using native query
        messageReactionRepository.insertReaction(
            messageId = request.messageId,
            userId = request.userId,
            emoji = request.emoji
        )
    }

    @Transactional
    override fun removeReaction(messageId: UUID, userId: UUID, emoji: String) {
        // Validate message exists
        if (!messageRepository.existsById(messageId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")
        }

        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        // Delete reaction using native query
        messageReactionRepository.deleteReaction(
            messageId = messageId,
            userId = userId,
            emoji = emoji
        )
    }

    @Transactional
    override fun markAsRead(messageId: UUID, userId: UUID) {
        // Validate message exists
        val message = messageRepository.findById(messageId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found") }

        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        // Validate user is participant in the chat
        val participant = chatParticipantRepository.findByChatIdAndUserId(message.chatId, userId)
        if (participant.isEmpty) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a participant in this chat")
        }

        // Mark as read using native query
        messageReadRepository.markAsRead(messageId, userId)

        // Update participant's last_read_message_id
        chatParticipantRepository.updateLastReadMessageId(message.chatId, userId, messageId)
    }
}

