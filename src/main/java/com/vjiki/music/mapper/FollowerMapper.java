package com.vjiki.music.mapper;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.entity.UserFollow;

public final class FollowerMapper {

    private FollowerMapper() {
    }

    public static FollowerResponse toResponse(UserFollow follow) {
        return FollowerResponse.builder()
                .followerId(follow.getFollowerId())
                .followerEmail(follow.getFollower() == null ? "" : follow.getFollower().getEmail())
                .followerNickname(follow.getFollower() == null ? "" : follow.getFollower().getNickname())
                .followerAvatarUrl(follow.getFollower() == null ? null : follow.getFollower().getAvatarUrl())
                .followedAt(follow.getCreatedAt())
                .build();
    }
}
