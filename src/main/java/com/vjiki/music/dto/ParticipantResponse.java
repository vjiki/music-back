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
public class ParticipantResponse {
    private UUID userId;
    private String userEmail;
    private String userNickname;
    private String userAvatarUrl;
    private String role;
    private OffsetDateTime joinedAt;

    @JsonProperty("isMuted")
    private Boolean isMuted;
}
