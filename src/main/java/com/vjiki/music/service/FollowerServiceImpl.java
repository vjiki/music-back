package com.vjiki.music.service;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.entity.UserFollow;
import com.vjiki.music.mapper.FollowerMapper;
import com.vjiki.music.repository.UserFollowRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FollowerServiceImpl implements FollowerService {

    private final UserFollowRepository userFollowRepository;
    private final FollowerMapper followerMapper;

    public FollowerServiceImpl(UserFollowRepository userFollowRepository,
                              FollowerMapper followerMapper) {
        this.userFollowRepository = userFollowRepository;
        this.followerMapper = followerMapper;
    }

    @Override
    public List<FollowerResponse> getFollowersByUserId(UUID userId) {
        List<UserFollow> userFollows = userFollowRepository.findByFollowedId(userId);
        
        return userFollows.stream()
                .map(followerMapper::toResponse)
                .toList();
    }
}

