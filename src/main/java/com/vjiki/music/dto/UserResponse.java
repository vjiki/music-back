package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String accessLevel;
    private Boolean isActive;
    private Boolean isVerified;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
}

