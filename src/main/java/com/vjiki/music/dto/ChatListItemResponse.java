package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Instagram-style chat list item response
 * Optimized for mobile chat list UI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListItemResponse {
    private UUID chatId;
    private String chatType;
    private String title;
    private String avatarUrl;
    private String lastMessagePreview;
    private OffsetDateTime lastMessageAt;
    private UUID lastMessageSenderId;
    private String lastMessageSenderName;
    private Integer unreadCount;
    private Boolean isMuted;
    private OffsetDateTime updatedAt;
    private List<ParticipantSummaryResponse> participants;
}

