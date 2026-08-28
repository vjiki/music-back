package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.mapper.FollowerMapper;
import com.vjiki.music.repository.UserFollowRepository;

@Service
public class FollowerServiceImpl implements FollowerService {

    private final UserFollowRepository userFollowRepository;

    public FollowerServiceImpl(UserFollowRepository userFollowRepository) {
        this.userFollowRepository = userFollowRepository;
    }

    @Override
    public List<FollowerResponse> getFollowersByUserId(UUID userId) {
        return userFollowRepository.findByFollowedId(userId).stream()
                .map(FollowerMapper::toResponse)
                .toList();
    }
}
