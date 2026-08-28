package com.vjiki.music.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowerResponse {
    private UUID followerId;
    private String followerEmail;
    private String followerNickname;
    private String followerAvatarUrl;
    private OffsetDateTime followedAt;
}
