package com.vjiki.music.service;

import com.vjiki.music.dto.SongLikeResponse;
import com.vjiki.music.entity.Dislike;
import com.vjiki.music.entity.Like;
import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.SongRepository;
import com.vjiki.music.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SongLikeServiceImpl implements SongLikeService {

    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public SongLikeServiceImpl(
            LikeRepository likeRepository,
            DislikeRepository dislikeRepository,
            SongRepository songRepository,
            UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.dislikeRepository = dislikeRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
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
}

