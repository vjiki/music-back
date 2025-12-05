package com.vjiki.music.service

import com.vjiki.music.dto.ChatListItemResponse
import com.vjiki.music.dto.ParticipantSummaryResponse
import com.vjiki.music.entity.ChatType
import com.vjiki.music.repository.ChatParticipantRepository
import com.vjiki.music.repository.ChatRepository
import com.vjiki.music.repository.MessageRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatListServiceImpl(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val messageRepository: MessageRepository
) : ChatListService {

    override fun getChatListForUser(userId: UUID): List<ChatListItemResponse> {
        val chats = chatRepository.findChatsByUserId(userId)
        return chats.map { chat -> mapToChatListItem(chat, userId) }
    }

    private fun mapToChatListItem(chat: com.vjiki.music.entity.Chat, currentUserId: UUID): ChatListItemResponse {
        val lastMessages = messageRepository.findMessagesByChatId(chat.id)
        val lastMessage = lastMessages.firstOrNull { it.isDeleted != true }

        val participants = chatParticipantRepository.findByChatId(chat.id)

        val participantSummaries = participants
            .filter { it.userId != currentUserId }
            .map { participant ->
                ParticipantSummaryResponse(
                    userId = participant.userId,
                    userNickname = participant.user?.nickname,
                    userAvatarUrl = participant.user?.avatarUrl
                )
            }

        val currentUserParticipant = participants.firstOrNull { it.userId == currentUserId }

        var displayTitle = chat.title
        if (chat.type == ChatType.DIRECT && displayTitle == null) {
            displayTitle = participantSummaries.firstOrNull()?.userNickname ?: "Direct Message"
        }

        return ChatListItemResponse(
            chatId = chat.id,
            chatType = chat.type.name,
            title = displayTitle,
            avatarUrl = chat.avatarUrl,
            lastMessagePreview = lastMessage?.content,
            lastMessageAt = lastMessage?.createdAt ?: chat.updatedAt,
            lastMessageSenderId = lastMessage?.senderId,
            lastMessageSenderName = lastMessage?.sender?.nickname,
            unreadCount = 0, // TODO: Implement unread count logic
            isMuted = currentUserParticipant?.isMuted ?: false,
            updatedAt = chat.updatedAt,
            participants = participantSummaries
        )
    }
}

