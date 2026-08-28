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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private UUID id;
    private String type;
    private String title;
    private String description;
    private String avatarUrl;
    private UUID ownerId;
    private String ownerNickname;

    @JsonProperty("isEncrypted")
    private Boolean isEncrypted;

    @JsonProperty("isArchived")
    private Boolean isArchived;

    @JsonProperty("isMuted")
    private Boolean isMuted;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Builder.Default
    private List<ParticipantResponse> participants = new ArrayList<>();
}
