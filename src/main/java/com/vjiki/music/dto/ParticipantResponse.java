package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {
    private UUID userId;
    private String userEmail;
    private String userNickname;
    private String userAvatarUrl;
    private String role;
    private OffsetDateTime joinedAt;
    private Boolean isMuted;
}

