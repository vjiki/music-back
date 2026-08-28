package com.vjiki.music.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instagram-style chat list item response
 * Optimized for mobile chat list UI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @JsonProperty("isMuted")
    private Boolean isMuted;

    private OffsetDateTime updatedAt;

    @Builder.Default
    private List<ParticipantSummaryResponse> participants = new ArrayList<>();
}
