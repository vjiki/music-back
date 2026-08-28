package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.FollowerResponse;

public interface FollowerService {

    List<FollowerResponse> getFollowersByUserId(UUID userId);
}
