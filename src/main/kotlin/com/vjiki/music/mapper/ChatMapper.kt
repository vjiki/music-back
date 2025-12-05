package com.vjiki.music.mapper

import com.vjiki.music.dto.ChatResponse
import com.vjiki.music.dto.ParticipantResponse
import com.vjiki.music.entity.Chat
import com.vjiki.music.entity.ChatParticipant
import org.springframework.stereotype.Component

@Component
object ChatMapper {
    fun Chat.toResponse(): ChatResponse {
        return ChatResponse(
            id = id,
            type = type.name,
            title = title,
            description = description,
            avatarUrl = avatarUrl,
            ownerId = ownerId,
            ownerNickname = owner?.nickname,
            isEncrypted = isEncrypted,
            isArchived = isArchived,
            isMuted = isMuted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            participants = participants.map { it.toParticipantResponse() }
        )
    }

    private fun ChatParticipant.toParticipantResponse(): ParticipantResponse {
        return ParticipantResponse(
            userId = userId,
            userEmail = user?.email ?: "",
            userNickname = user?.nickname ?: "",
            userAvatarUrl = user?.avatarUrl,
            role = role.name,
            joinedAt = joinedAt,
            isMuted = isMuted
        )
    }
}

