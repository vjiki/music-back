package com.vjiki.music.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @JsonProperty("isEdited")
    private Boolean isEdited;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
