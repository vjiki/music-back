package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerResponse {
    private UUID followerId;
    private String followerEmail;
    private String followerNickname;
    private String followerAvatarUrl;
    private OffsetDateTime followedAt;
}

