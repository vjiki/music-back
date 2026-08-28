package com.vjiki.music.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;

@DisplayName("PlaylistController integration tests")
class PlaylistControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    private User createTestUser() {
        return userRepository.saveAndFlush(User.builder()
                .email("controlleruser" + UUID.randomUUID() + "@example.com")
                .nickname("controlleruser")
                .accessLevel(AccessLevel.USER)
                .provider(AuthProvider.LOCAL)
                .isActive(true)
                .isVerified(false)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    private Playlist createPlaylist(User user, String name) {
        return playlistRepository.saveAndFlush(Playlist.builder()
                .user(user)
                .name(name)
                .type("CUSTOM")
                .isPublic(false)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    @Nested
    @DisplayName("PlaylistController Integration Tests")
    class PlaylistEndpoints {

        @Test
        @DisplayName("should get playlists by user id")
        void shouldGetPlaylistsByUserId() throws Exception {
            User user = createTestUser();
            Playlist playlist = createPlaylist(user, "Test Playlist");

            mockMvc.perform(get("/api/v1/playlists/user/{userId}", user.getId())
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(playlist.getId().toString()))
                    .andExpect(jsonPath("$[0].name").value("Test Playlist"))
                    .andExpect(jsonPath("$[0].userId").value(user.getId().toString()));
        }

        @Test
        @DisplayName("should return empty list when user has no playlists")
        void shouldReturnEmptyListWhenUserHasNoPlaylists() throws Exception {
            User user = createTestUser();

            mockMvc.perform(get("/api/v1/playlists/user/{userId}", user.getId())
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("should get playlist with songs by playlist id")
        void shouldGetPlaylistWithSongsByPlaylistId() throws Exception {
            User user = createTestUser();
            Playlist playlist = createPlaylist(user, "My Playlist");

            mockMvc.perform(get("/api/v1/playlists/{playlistId}", playlist.getId())
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(playlist.getId().toString()))
                    .andExpect(jsonPath("$.name").value("My Playlist"))
                    .andExpect(jsonPath("$.songs").isArray());
        }

        @Test
        @DisplayName("should fail the request when playlist not found")
        void shouldFailWhenPlaylistNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            // The application declares no exception handler, so the failure surfaces as a servlet
            // level error instead of a rendered 5xx response body.
            assertThatThrownBy(() -> mockMvc.perform(
                    get("/api/v1/playlists/{playlistId}", nonExistentId).with(user("test").roles("USER"))))
                    .rootCause()
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Playlist not found");
        }
    }
}
