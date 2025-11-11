package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private UUID id;
    private String type;
    private String title;
    private String description;
    private String avatarUrl;
    private UUID ownerId;
    private String ownerNickname;
    private Boolean isEncrypted;
    private Boolean isArchived;
    private Boolean isMuted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<ParticipantResponse> participants;
}

