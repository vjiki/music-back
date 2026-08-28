package com.vjiki.music.mapper;

import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .accessLevel(user.getAccessLevel() == null ? null : user.getAccessLevel().name())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
