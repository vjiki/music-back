package com.vjiki.music.mapper;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.entity.UserFollow;
import org.springframework.stereotype.Component;

@Component
public class FollowerMapperImpl implements FollowerMapper {

    @Override
    public FollowerResponse toResponse(UserFollow userFollow) {
        if (userFollow.getFollower() == null) {
            return new FollowerResponse(
                    userFollow.getFollowerId(),
                    null,
                    null,
                    null,
                    userFollow.getCreatedAt()
            );
        }
        
        return new FollowerResponse(
                userFollow.getFollowerId(),
                userFollow.getFollower().getEmail(),
                userFollow.getFollower().getNickname(),
                userFollow.getFollower().getAvatarUrl(),
                userFollow.getCreatedAt()
        );
    }
}

