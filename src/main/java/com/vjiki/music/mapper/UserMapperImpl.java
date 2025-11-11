package com.vjiki.music.mapper;

import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getAccessLevel() != null ? user.getAccessLevel().name() : null,
                user.getIsActive(),
                user.getIsVerified(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }
}

