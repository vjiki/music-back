package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.dto.PlaylistWithSongsResponse;
import com.vjiki.music.dto.TagResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.PlaylistSong;
import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.PlaylistSongRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistSongRepository playlistSongRepository;

    @Mock
    private TagLookupService tagLookupService;

    @InjectMocks
    private PlaylistServiceImpl playlistService;

    private User user(UUID userId) {
        return User.builder()
                .id(userId)
                .email("test@example.com")
                .nickname("testuser")
                .provider(AuthProvider.LOCAL)
                .accessLevel(AccessLevel.USER)
                .isActive(true)
                .createdBy("system")
                .modifiedBy("system")
                .build();
    }

    private Song song(UUID songId) {
        return Song.builder()
                .id(songId)
                .artists(Map.of("default", List.of("Artist")))
                .audioUrls(Map.of("default", "http://audio.com"))
                .coverUrls(Map.of("default", "http://cover.com"))
                .title("Test Song")
                .active(true)
                .createdBy("system")
                .modifiedBy("system")
                .build();
    }

    @Nested
    @DisplayName("getPlaylistsByUserId")
    class GetPlaylistsByUserId {

        @Test
        @DisplayName("should return playlists for user")
        void shouldReturnPlaylistsForUser() {
            UUID userId = UUID.randomUUID();
            UUID playlistId = UUID.randomUUID();
            Playlist playlist = Playlist.builder()
                    .id(playlistId)
                    .user(user(userId))
                    .name("My Playlist")
                    .type("CUSTOM")
                    .isPublic(false)
                    .build();

            when(playlistRepository.findByUserIdWithUser(userId)).thenReturn(List.of(playlist));

            List<PlaylistResponse> result = playlistService.getPlaylistsByUserId(userId);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(playlistId);
            assertThat(result.getFirst().getName()).isEqualTo("My Playlist");
            assertThat(result.getFirst().getUserId()).isEqualTo(userId);
            assertThat(result.getFirst().getUserNickname()).isEqualTo("testuser");
            verify(playlistRepository).findByUserIdWithUser(userId);
        }

        @Test
        @DisplayName("should return empty list when user has no playlists")
        void shouldReturnEmptyListWhenUserHasNoPlaylists() {
            UUID userId = UUID.randomUUID();
            when(playlistRepository.findByUserIdWithUser(userId)).thenReturn(List.of());

            List<PlaylistResponse> result = playlistService.getPlaylistsByUserId(userId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPlaylistWithSongs")
    class GetPlaylistWithSongs {

        @Test
        @DisplayName("should return playlist with songs enriched with tags")
        void shouldReturnPlaylistWithSongs() {
            UUID playlistId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID songId = UUID.randomUUID();
            User user = user(userId);
            Playlist playlist = Playlist.builder()
                    .id(playlistId)
                    .user(user)
                    .name("My Playlist")
                    .type("CUSTOM")
                    .isPublic(false)
                    .build();
            PlaylistSong playlistSong = PlaylistSong.builder()
                    .id(UUID.randomUUID())
                    .playlist(playlist)
                    .song(song(songId))
                    .position(0)
                    .addedBy(user)
                    .build();

            when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
            when(playlistSongRepository.findByPlaylistIdWithSong(playlistId)).thenReturn(List.of(playlistSong));
            when(tagLookupService.getTagsByTrackIds(List.of(songId)))
                    .thenReturn(Map.of(songId, List.of(new TagResponse("rock"))));

            PlaylistWithSongsResponse result = playlistService.getPlaylistWithSongs(playlistId);

            assertThat(result.getId()).isEqualTo(playlistId);
            assertThat(result.getName()).isEqualTo("My Playlist");
            assertThat(result.getSongs()).hasSize(1);
            assertThat(result.getSongs().getFirst().getSongId()).isEqualTo(songId);
            assertThat(result.getSongs().getFirst().getSongTitle()).isEqualTo("Test Song");
            assertThat(result.getSongs().getFirst().getTags())
                    .extracting(TagResponse::getName)
                    .containsExactly("rock");
            verify(playlistRepository).findById(playlistId);
            verify(playlistSongRepository).findByPlaylistIdWithSong(playlistId);
        }

        @Test
        @DisplayName("should return playlist with an empty song list when it has no songs")
        void shouldReturnPlaylistWithoutSongs() {
            UUID playlistId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Playlist playlist = Playlist.builder()
                    .id(playlistId)
                    .user(user(userId))
                    .name("Empty Playlist")
                    .type("CUSTOM")
                    .isPublic(false)
                    .build();

            when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
            when(playlistSongRepository.findByPlaylistIdWithSong(playlistId)).thenReturn(List.of());
            when(tagLookupService.getTagsByTrackIds(anyList())).thenReturn(Map.of());

            PlaylistWithSongsResponse result = playlistService.getPlaylistWithSongs(playlistId);

            assertThat(result.getId()).isEqualTo(playlistId);
            assertThat(result.getSongs()).isEmpty();
        }

        @Test
        @DisplayName("should throw exception when playlist not found")
        void shouldThrowWhenPlaylistNotFound() {
            UUID playlistId = UUID.randomUUID();
            when(playlistRepository.findById(playlistId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> playlistService.getPlaylistWithSongs(playlistId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Playlist not found: " + playlistId);

            verify(playlistSongRepository, never()).findByPlaylistIdWithSong(playlistId);
        }
    }
}
