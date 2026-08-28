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
public class MessageReactionResponse {
    private UUID messageId;
    private UUID userId;
    private String emoji;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
