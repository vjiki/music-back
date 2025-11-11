package com.vjiki.music.mapper;

import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.User;

public interface UserMapper {
    UserResponse toResponse(User user);
}

