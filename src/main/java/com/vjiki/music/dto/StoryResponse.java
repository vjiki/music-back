package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Instagram-style story response
 * Optimized for mobile story UI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Boolean isExpired;
}

