package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String senderEmail;
    private String senderNickname;
    private String senderAvatarUrl;
    private UUID replyToId;
    private String messageType;
    private String content;
    private UUID songId;
    private Integer attachmentCount;
    private Boolean isEdited;
    private Boolean isDeleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

