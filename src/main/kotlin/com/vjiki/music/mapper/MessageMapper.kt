package com.vjiki.music.mapper

import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.entity.Message
import org.springframework.stereotype.Component

@Component
object MessageMapper {
    fun Message.toResponse(): MessageResponse {
        return MessageResponse(
            id = id,
            chatId = chatId,
            senderId = senderId,
            senderEmail = sender?.email,
            senderNickname = sender?.nickname,
            senderAvatarUrl = sender?.avatarUrl,
            replyToId = replyToId,
            messageType = messageType.name,
            content = content,
            songId = songId,
            attachmentCount = attachmentCount,
            isEdited = isEdited,
            isDeleted = isDeleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

