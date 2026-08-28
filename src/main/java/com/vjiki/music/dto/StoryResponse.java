package com.vjiki.music.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instagram-style story response
 * Optimized for mobile story UI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryResponse {
    private UUID id;
    private UUID userId;
    private String userNickname;
    private String userAvatarUrl;
    private String imageUrl;
    private String previewUrl;
    private String storyType;
    private UUID songId;
    private String songTitle;
    private String songArtist;
    private String caption;
    private String location;
    private Integer viewsCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;

    @JsonProperty("isExpired")
    private Boolean isExpired;
}
