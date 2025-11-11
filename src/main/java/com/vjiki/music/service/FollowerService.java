package com.vjiki.music.service;

import com.vjiki.music.dto.FollowerResponse;

import java.util.List;
import java.util.UUID;

public interface FollowerService {
    List<FollowerResponse> getFollowersByUserId(UUID userId);
}

