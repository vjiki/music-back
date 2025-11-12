package com.vjiki.music.service;

import com.vjiki.music.dto.SongLikeResponse;
import com.vjiki.music.entity.Dislike;
import com.vjiki.music.entity.Like;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.PlaylistSong;
import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.PlaylistSongRepository;
import com.vjiki.music.repository.SongRepository;
import com.vjiki.music.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SongLikeServiceImpl implements SongLikeService {

    private static final String DEFAULT_LIKES_PLAYLIST_NAME = "DEFAULT_LIKES";
    private static final String DEFAULT_DISLIKES_PLAYLIST_NAME = "DEFAULT_DISLIKES";

    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;

    public SongLikeServiceImpl(
            LikeRepository likeRepository,
            DislikeRepository dislikeRepository,
            SongRepository songRepository,
            UserRepository userRepository,
            PlaylistRepository playlistRepository,
            PlaylistSongRepository playlistSongRepository) {
        this.likeRepository = likeRepository;
        this.dislikeRepository = dislikeRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
    }

    @Override
    @Transactional
    public void likeSong(UUID userId, UUID songId) {
        // Check if user already liked
        if (likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)) {
            return; // already liked
        }

        // Revoke dislike if exists
        Optional<Dislike> existingDislike = dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        if (existingDislike.isPresent()) {
            Dislike dislike = existingDislike.get();
            dislike.setRevokedAt(OffsetDateTime.now());
            dislikeRepository.save(dislike);
            // Remove from DEFAULT_DISLIKES playlist
            removeSongFromPlaylist(userId, songId, DEFAULT_DISLIKES_PLAYLIST_NAME);
        }

        // Save new like
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));

        Like like = Like.builder()
                .user(user)
                .song(song)
                .createdBy("system")
                .build();
        likeRepository.save(like);

        // Add to DEFAULT_LIKES playlist
        addSongToPlaylist(user, song, DEFAULT_LIKES_PLAYLIST_NAME);

        // Update song counts (optional - can be done via triggers in DB)
        updateSongCounts(songId);
    }

    @Override
    @Transactional
    public void dislikeSong(UUID userId, UUID songId) {
        // Check if user already disliked
        if (dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)) {
            return; // already disliked
        }

        // Revoke like if exists
        Optional<Like> existingLike = likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            like.setRevokedAt(OffsetDateTime.now());
            likeRepository.save(like);
            // Remove from DEFAULT_LIKES playlist
            removeSongFromPlaylist(userId, songId, DEFAULT_LIKES_PLAYLIST_NAME);
        }

        // Save new dislike
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));

        Dislike dislike = Dislike.builder()
                .user(user)
                .song(song)
                .createdBy("system")
                .build();
        dislikeRepository.save(dislike);

        // Add to DEFAULT_DISLIKES playlist
        addSongToPlaylist(user, song, DEFAULT_DISLIKES_PLAYLIST_NAME);

        // Update song counts (optional - can be done via triggers in DB)
        updateSongCounts(songId);
    }

    @Override
    public SongLikeResponse getLikeDislikeInfo(UUID userId, UUID songId) {
        boolean isLiked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        boolean isDisliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);

        // Calculate counts dynamically for real-time accuracy
        long likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(songId);
        long dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(songId);

        return new SongLikeResponse(isLiked, isDisliked, likesCount, dislikesCount);
    }

    private void updateSongCounts(UUID songId) {
        Optional<Song> songOpt = songRepository.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            long likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(songId);
            long dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(songId);
            song.setLikesCount(likesCount);
            song.setDislikesCount(dislikesCount);
            songRepository.save(song);
        }
    }

    private Playlist findOrCreateDefaultPlaylist(User user, String playlistName) {
        return playlistRepository.findByUserIdAndName(user.getId(), playlistName)
                .orElseGet(() -> {
                    Playlist playlist = Playlist.builder()
                            .user(user)
                            .name(playlistName)
                            .type("DEFAULT")
                            .isPublic(false)
                            .createdBy("system")
                            .modifiedBy("system")
                            .build();
                    return playlistRepository.save(playlist);
                });
    }

    private void addSongToPlaylist(User user, Song song, String playlistName) {
        Playlist playlist = findOrCreateDefaultPlaylist(user, playlistName);
        
        // Check if song already exists in playlist
        Optional<PlaylistSong> existingPlaylistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlist.getId(), song.getId());
        if (existingPlaylistSong.isPresent()) {
            return; // Already in playlist
        }

        // Get the maximum position in the playlist
        int maxPosition = playlistSongRepository.findByPlaylistIdWithSong(playlist.getId())
                .stream()
                .mapToInt(PlaylistSong::getPosition)
                .max()
                .orElse(-1);

        PlaylistSong playlistSong = PlaylistSong.builder()
                .playlist(playlist)
                .song(song)
                .position(maxPosition + 1)
                .addedBy(user)
                .build();
        playlistSongRepository.save(playlistSong);
    }

    private void removeSongFromPlaylist(UUID userId, UUID songId, String playlistName) {
        Optional<Playlist> playlistOpt = playlistRepository.findByUserIdAndName(userId, playlistName);
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            playlistSongRepository.deleteByPlaylistIdAndSongId(playlist.getId(), songId);
        }
    }
}

