package com.vjiki.music.mapper;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.entity.UserFollow;

public interface FollowerMapper {
    FollowerResponse toResponse(UserFollow userFollow);
}

