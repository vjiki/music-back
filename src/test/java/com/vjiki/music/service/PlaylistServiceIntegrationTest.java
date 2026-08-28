package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.support.AbstractIntegrationTest;

class PlaylistServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PlaylistServiceImpl playlistService;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    private User createTestUser() {
        User user = User.builder()
                .email("playlistuser" + UUID.randomUUID() + "@example.com")
                .nickname("playlistuser")
                .accessLevel(AccessLevel.USER)
                .provider(AuthProvider.LOCAL)
                .isActive(true)
                .isVerified(false)
                .createdBy("system")
                .modifiedBy("system")
                .build();
        User saved = userRepository.save(user);
        userRepository.flush();
        return saved;
    }

    private Playlist createPlaylist(User user, String name) {
        Playlist playlist = Playlist.builder()
                .user(user)
                .name(name)
                .type("CUSTOM")
                .isPublic(false)
                .createdBy("system")
                .modifiedBy("system")
                .build();
        Playlist saved = playlistRepository.save(playlist);
        playlistRepository.flush();
        return saved;
    }

    @Nested
    @DisplayName("PlaylistService Integration Tests")
    class PlaylistServiceIntegrationTests {

        @Test
        @DisplayName("should get playlists by user id")
        void shouldGetPlaylistsByUserId() {
            User user = createTestUser();
            createPlaylist(user, "Playlist 1");
            createPlaylist(user, "Playlist 2");

            List<PlaylistResponse> result = playlistService.getPlaylistsByUserId(user.getId());

            assertThat(result).hasSize(2);
            assertThat(result).extracting(PlaylistResponse::getName)
                    .containsExactly("Playlist 2", "Playlist 1");
        }

        @Test
        @DisplayName("should return empty list when user has no playlists")
        void shouldReturnEmptyListWhenUserHasNoPlaylists() {
            User user = createTestUser();

            List<PlaylistResponse> result = playlistService.getPlaylistsByUserId(user.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should get playlist with songs")
        void shouldGetPlaylistWithSongs() {
            User user = createTestUser();
            Playlist playlist = createPlaylist(user, "My Playlist");

            PlaylistWithSongsResponse result = playlistService.getPlaylistWithSongs(playlist.getId());

            assertThat(result.getId()).isEqualTo(playlist.getId());
            assertThat(result.getName()).isEqualTo("My Playlist");
            assertThat(result.getUserId()).isEqualTo(user.getId());
            assertThat(result.getSongs()).isEmpty();
        }

        @Test
        @DisplayName("should throw exception when playlist not found")
        void shouldThrowExceptionWhenPlaylistNotFound() {
            Throwable exception = catchThrowable(() -> playlistService.getPlaylistWithSongs(UUID.randomUUID()));

            assertThat(exception).isNotNull();
            assertThat(exception).hasMessageContaining("not found");
        }
    }
}
