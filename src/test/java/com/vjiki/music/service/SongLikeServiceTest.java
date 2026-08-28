package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import com.vjiki.music.dto.SongLikeResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Dislike;
import com.vjiki.music.entity.Like;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.PlaylistSongRepository;
import com.vjiki.music.repository.SongRepository;

@ExtendWith(MockitoExtension.class)
class SongLikeServiceTest {

    private static final String DEFAULT_LIKES = "DEFAULT_LIKES";
    private static final String DEFAULT_DISLIKES = "DEFAULT_DISLIKES";

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private DislikeRepository dislikeRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistSongRepository playlistSongRepository;

    @InjectMocks
    private SongLikeServiceImpl songLikeService;

    private final UUID userId = UUID.randomUUID();
    private final UUID songId = UUID.randomUUID();

    private User user() {
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

    private Song song() {
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

    private Playlist playlist(UUID playlistId, String name) {
        return Playlist.builder()
                .id(playlistId)
                .user(user())
                .name(name)
                .type("DEFAULT")
                .isPublic(false)
                .build();
    }

    @Nested
    @DisplayName("likeSong")
    class LikeSong {

        @Test
        @DisplayName("should insert a like and add the song to the default likes playlist")
        void shouldLikeSong() {
            Song song = song();
            when(dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.empty());
            when(songRepository.findById(songId)).thenReturn(Optional.of(song));
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(1L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(0L);

            songLikeService.likeSong(userId, songId);

            verify(likeRepository).insertLike(userId, songId, "system");
            verify(playlistSongRepository).addSongToPlaylistIfNotExists(userId, songId, DEFAULT_LIKES);
            verify(songRepository).save(song);
            assertThat(song.getLikesCount()).isEqualTo(1L);
            assertThat(song.getDislikesCount()).isZero();
        }

        @Test
        @DisplayName("should not touch dislikes when the song was never disliked")
        void shouldNotTouchDislikesWhenNoneExists() {
            when(dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.empty());
            when(songRepository.findById(songId)).thenReturn(Optional.of(song()));
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(1L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(0L);

            songLikeService.likeSong(userId, songId);

            verify(dislikeRepository, never()).save(any());
            verify(playlistRepository, never()).findByUserIdAndName(any(), any());
            verify(playlistSongRepository, never()).deleteByPlaylistIdAndSongId(any(), any());
        }

        @Test
        @DisplayName("should revoke an existing dislike and drop the song from the dislikes playlist")
        void shouldRevokeDislikeWhenLiking() {
            UUID dislikesPlaylistId = UUID.randomUUID();
            Dislike dislike = Dislike.builder()
                    .id(UUID.randomUUID())
                    .user(user())
                    .song(song())
                    .createdBy("system")
                    .build();
            Song song = song();

            when(dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.of(dislike));
            when(playlistRepository.findByUserIdAndName(userId, DEFAULT_DISLIKES))
                    .thenReturn(Optional.of(playlist(dislikesPlaylistId, DEFAULT_DISLIKES)));
            when(songRepository.findById(songId)).thenReturn(Optional.of(song));
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(1L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(0L);

            songLikeService.likeSong(userId, songId);

            assertThat(dislike.getRevokedAt()).isNotNull();
            verify(dislikeRepository).save(dislike);
            verify(playlistSongRepository).deleteByPlaylistIdAndSongId(dislikesPlaylistId, songId);
            verify(likeRepository).insertLike(userId, songId, "system");
        }

        @Test
        @DisplayName("should still insert the like when the song row cannot be found for count refresh")
        void shouldSkipCountUpdateWhenSongMissing() {
            when(dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.empty());
            when(songRepository.findById(songId)).thenReturn(Optional.empty());

            songLikeService.likeSong(userId, songId);

            verify(likeRepository).insertLike(userId, songId, "system");
            verify(songRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("dislikeSong")
    class DislikeSong {

        @Test
        @DisplayName("should insert a dislike and add the song to the default dislikes playlist")
        void shouldDislikeSong() {
            Song song = song();
            when(likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.empty());
            when(songRepository.findById(songId)).thenReturn(Optional.of(song));
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(0L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(1L);

            songLikeService.dislikeSong(userId, songId);

            verify(dislikeRepository).insertDislike(userId, songId, "system");
            verify(playlistSongRepository).addSongToPlaylistIfNotExists(userId, songId, DEFAULT_DISLIKES);
            verify(songRepository).save(song);
            assertThat(song.getLikesCount()).isZero();
            assertThat(song.getDislikesCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should not touch likes when the song was never liked")
        void shouldNotTouchLikesWhenNoneExists() {
            when(likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.empty());
            when(songRepository.findById(songId)).thenReturn(Optional.of(song()));
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(0L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(1L);

            songLikeService.dislikeSong(userId, songId);

            verify(likeRepository, never()).save(any());
            verify(playlistRepository, never()).findByUserIdAndName(any(), any());
            verify(playlistSongRepository, never()).deleteByPlaylistIdAndSongId(any(), any());
        }

        @Test
        @DisplayName("should revoke an existing like and drop the song from the likes playlist")
        void shouldRevokeLikeWhenDisliking() {
            UUID likesPlaylistId = UUID.randomUUID();
            Like like = Like.builder()
                    .id(UUID.randomUUID())
                    .user(user())
                    .song(song())
                    .createdBy("system")
                    .build();

            when(likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.of(like));
            when(playlistRepository.findByUserIdAndName(userId, DEFAULT_LIKES))
                    .thenReturn(Optional.of(playlist(likesPlaylistId, DEFAULT_LIKES)));
            when(songRepository.findById(songId)).thenReturn(Optional.of(song()));
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(0L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(1L);

            songLikeService.dislikeSong(userId, songId);

            assertThat(like.getRevokedAt()).isNotNull();
            verify(likeRepository).save(like);
            verify(playlistSongRepository).deleteByPlaylistIdAndSongId(likesPlaylistId, songId);
            verify(dislikeRepository).insertDislike(userId, songId, "system");
        }

        @Test
        @DisplayName("should still insert the dislike when the song row cannot be found for count refresh")
        void shouldSkipCountUpdateWhenSongMissing() {
            when(likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId))
                    .thenReturn(Optional.empty());
            when(songRepository.findById(songId)).thenReturn(Optional.empty());

            songLikeService.dislikeSong(userId, songId);

            verify(dislikeRepository).insertDislike(userId, songId, "system");
            verify(songRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getLikeDislikeInfo")
    class GetLikeDislikeInfo {

        @Test
        @DisplayName("should return correct like/dislike info")
        void shouldReturnLikeDislikeInfo() {
            when(likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)).thenReturn(true);
            when(dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)).thenReturn(false);
            when(likeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(10L);
            when(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId)).thenReturn(2L);

            SongLikeResponse result = songLikeService.getLikeDislikeInfo(userId, songId);

            assertThat(result.getIsLiked()).isTrue();
            assertThat(result.getIsDisliked()).isFalse();
            assertThat(result.getLikesCount()).isEqualTo(10L);
            assertThat(result.getDislikesCount()).isEqualTo(2L);
        }
    }
}
