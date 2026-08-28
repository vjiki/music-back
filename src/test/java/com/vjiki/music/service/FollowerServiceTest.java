package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.User;
import com.vjiki.music.entity.UserFollow;
import com.vjiki.music.repository.UserFollowRepository;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {

    @Mock
    private UserFollowRepository userFollowRepository;

    @InjectMocks
    private FollowerServiceImpl followerService;

    private User testUser(String email, String nickname) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickname(nickname)
                .accessLevel(AccessLevel.USER)
                .provider(AuthProvider.LOCAL)
                .isActive(true)
                .isVerified(false)
                .createdBy("system")
                .modifiedBy("system")
                .build();
    }

    private UserFollow follow(User follower, User followed) {
        return UserFollow.builder()
                .followerId(follower.getId())
                .followedId(followed.getId())
                .follower(follower)
                .followed(followed)
                .build();
    }

    @Nested
    @DisplayName("getFollowersByUserId")
    class GetFollowersByUserId {

        @Test
        @DisplayName("should return followers for user")
        void shouldReturnFollowersForUser() {
            UUID userId = UUID.randomUUID();
            User follower1 = testUser("follower1@example.com", "follower1");
            User follower2 = testUser("follower2@example.com", "follower2");
            User followed = testUser("followed@example.com", "followed");

            when(userFollowRepository.findByFollowedId(userId))
                    .thenReturn(List.of(follow(follower1, followed), follow(follower2, followed)));

            List<FollowerResponse> result = followerService.getFollowersByUserId(userId);

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(FollowerResponse::getFollowerNickname)
                    .containsExactly("follower1", "follower2");
            assertThat(result.getFirst().getFollowerId()).isEqualTo(follower1.getId());
            assertThat(result.getFirst().getFollowerEmail()).isEqualTo("follower1@example.com");
            verify(userFollowRepository).findByFollowedId(userId);
        }

        @Test
        @DisplayName("should return empty list when no followers")
        void shouldReturnEmptyListWhenNoFollowers() {
            UUID userId = UUID.randomUUID();
            when(userFollowRepository.findByFollowedId(userId)).thenReturn(List.of());

            List<FollowerResponse> result = followerService.getFollowersByUserId(userId);

            assertThat(result).isEmpty();
            verify(userFollowRepository).findByFollowedId(userId);
        }
    }
}
