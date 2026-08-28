package com.vjiki.music.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.vjiki.music.dto.SongLikeRequest;
import com.vjiki.music.dto.SongLikeResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Like;
import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.SongRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.service.SongLikeService;

@DisplayName("SongLikeController integration tests")
class SongLikeControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private DislikeRepository dislikeRepository;

    @Autowired
    private SongLikeService songLikeService;

    private User createUser(String email, String nickname) {
        return userRepository.saveAndFlush(User.builder()
                .email(email)
                .nickname(nickname)
                .provider(AuthProvider.LOCAL)
                .accessLevel(AccessLevel.USER)
                .isActive(true)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    private Song createSong() {
        return songRepository.saveAndFlush(Song.builder()
                .artists(Map.of("default", List.of("Artist")))
                .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                .title("Test Song")
                .active(true)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    @Nested
    @DisplayName("POST /api/v1/song-likes/like")
    class LikeSong {

        @Test
        @DisplayName("should like a song")
        void shouldLikeASong() throws Exception {
            User user = createUser("test@example.com", "testuser");
            Song song = createSong();

            SongLikeRequest request = new SongLikeRequest(user.getId(), song.getId());

            mockMvc.perform(post("/api/v1/song-likes/like")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk());

            boolean isLiked = likeRepository
                    .existsByUserIdAndSongIdAndRevokedAtIsNull(user.getId(), song.getId());
            assertThat(isLiked).isTrue();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/song-likes/dislike")
    class DislikeSong {

        @Test
        @DisplayName("should dislike a song")
        void shouldDislikeASong() throws Exception {
            User user = createUser("test2@example.com", "testuser2");
            Song song = createSong();

            SongLikeRequest request = new SongLikeRequest(user.getId(), song.getId());

            mockMvc.perform(post("/api/v1/song-likes/dislike")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk());

            boolean isDisliked = dislikeRepository
                    .existsByUserIdAndSongIdAndRevokedAtIsNull(user.getId(), song.getId());
            assertThat(isDisliked).isTrue();
        }
    }

    @Nested
    @DisplayName("SongLikeService like/dislike info")
    class LikeDislikeInfo {

        @Test
        @DisplayName("should return like/dislike info")
        void shouldReturnLikeDislikeInfo() throws Exception {
            User user = createUser("test3@example.com", "testuser3");
            Song song = createSong();

            likeRepository.saveAndFlush(Like.builder()
                    .user(user)
                    .song(song)
                    .createdBy("system")
                    .build());

            SongLikeResponse info = songLikeService.getLikeDislikeInfo(user.getId(), song.getId());

            // There is no HTTP endpoint for this, so assert on the serialized wire format instead.
            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(info));
            assertThat(json.path("isLiked").asBoolean()).isTrue();
            assertThat(json.path("isDisliked").asBoolean()).isFalse();
            assertThat(json.has("likesCount")).isTrue();
            assertThat(json.has("dislikesCount")).isTrue();
        }
    }
}
