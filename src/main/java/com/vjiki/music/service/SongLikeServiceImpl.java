package com.vjiki.music.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vjiki.music.dto.SongLikeResponse;
import com.vjiki.music.entity.Dislike;
import com.vjiki.music.entity.Like;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.PlaylistSongRepository;
import com.vjiki.music.repository.SongRepository;

@Service
public class SongLikeServiceImpl implements SongLikeService {

    private static final String DEFAULT_LIKES_PLAYLIST_NAME = "DEFAULT_LIKES";
    private static final String DEFAULT_DISLIKES_PLAYLIST_NAME = "DEFAULT_DISLIKES";

    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;

    public SongLikeServiceImpl(LikeRepository likeRepository,
                               DislikeRepository dislikeRepository,
                               SongRepository songRepository,
                               PlaylistRepository playlistRepository,
                               PlaylistSongRepository playlistSongRepository) {
        this.likeRepository = likeRepository;
        this.dislikeRepository = dislikeRepository;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
    }

    @Override
    @Transactional
    public void likeSong(UUID userId, UUID songId) {
        Optional<Dislike> existingDislike =
                dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        if (existingDislike.isPresent()) {
            Dislike dislike = existingDislike.get();
            dislike.setRevokedAt(OffsetDateTime.now());
            dislikeRepository.save(dislike);
            removeSongFromPlaylist(userId, songId, DEFAULT_DISLIKES_PLAYLIST_NAME);
        }

        // Native insert lets PostgreSQL generate the UUID and timestamps.
        likeRepository.insertLike(userId, songId, "system");

        playlistSongRepository.addSongToPlaylistIfNotExists(userId, songId, DEFAULT_LIKES_PLAYLIST_NAME);
        updateSongCounts(songId);
    }

    @Override
    @Transactional
    public void dislikeSong(UUID userId, UUID songId) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            like.setRevokedAt(OffsetDateTime.now());
            likeRepository.save(like);
            removeSongFromPlaylist(userId, songId, DEFAULT_LIKES_PLAYLIST_NAME);
        }

        // Native insert lets PostgreSQL generate the UUID and timestamps.
        dislikeRepository.insertDislike(userId, songId, "system");

        playlistSongRepository.addSongToPlaylistIfNotExists(userId, songId, DEFAULT_DISLIKES_PLAYLIST_NAME);
        updateSongCounts(songId);
    }

    @Override
    public SongLikeResponse getLikeDislikeInfo(UUID userId, UUID songId) {
        boolean isLiked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        boolean isDisliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
        long likesCount = likeRepository.countBySongIdAndRevokedAtIsNull(songId);
        long dislikesCount = dislikeRepository.countBySongIdAndRevokedAtIsNull(songId);

        return new SongLikeResponse(isLiked, isDisliked, likesCount, dislikesCount);
    }

    private void updateSongCounts(UUID songId) {
        songRepository.findById(songId).ifPresent(song -> {
            song.setLikesCount(likeRepository.countBySongIdAndRevokedAtIsNull(songId));
            song.setDislikesCount(dislikeRepository.countBySongIdAndRevokedAtIsNull(songId));
            songRepository.save(song);
        });
    }

    private void removeSongFromPlaylist(UUID userId, UUID songId, String playlistName) {
        playlistRepository.findByUserIdAndName(userId, playlistName)
                .ifPresent(playlist -> playlistSongRepository.deleteByPlaylistIdAndSongId(playlist.getId(), songId));
    }
}
