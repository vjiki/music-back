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
public class UserResponse {
    private UUID id;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String accessLevel;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isVerified")
    private Boolean isVerified;

    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
}
