package com.vjiki.music.mapper;

import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.entity.Message;

public final class MessageMapper {

    private MessageMapper() {
    }

    public static MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .chatId(message.getChatId())
                .senderId(message.getSenderId())
                .senderEmail(message.getSender() == null ? null : message.getSender().getEmail())
                .senderNickname(message.getSender() == null ? null : message.getSender().getNickname())
                .senderAvatarUrl(message.getSender() == null ? null : message.getSender().getAvatarUrl())
                .replyToId(message.getReplyToId())
                .messageType(message.getMessageType() == null ? null : message.getMessageType().name())
                .content(message.getContent())
                .songId(message.getSongId())
                .attachmentCount(message.getAttachmentCount())
                .isEdited(message.getIsEdited())
                .isDeleted(message.getIsDeleted())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}
