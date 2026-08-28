package com.vjiki.music.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.vjiki.music.entity.Song;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.SongIdCountProjection;

/**
 * Per-user like/dislike state and counters for a batch of songs, resolved in a
 * fixed number of queries regardless of batch size.
 */
record SongEnrichment(
        Set<UUID> likedIds,
        Set<UUID> dislikedIds,
        Map<UUID, Long> likesCounts,
        Map<UUID, Long> dislikesCounts) {

    static SongEnrichment empty() {
        return new SongEnrichment(Set.of(), Set.of(), Map.of(), Map.of());
    }

    static SongEnrichment load(UUID userId,
                               List<UUID> songIds,
                               LikeRepository likeRepository,
                               DislikeRepository dislikeRepository) {
        if (songIds.isEmpty()) {
            return empty();
        }
        return new SongEnrichment(
                new HashSet<>(likeRepository.findActiveLikedSongIds(userId, songIds)),
                new HashSet<>(dislikeRepository.findActiveDislikedSongIds(userId, songIds)),
                toCountMap(likeRepository.countActiveLikesBySongIds(songIds)),
                toCountMap(dislikeRepository.countActiveDislikesBySongIds(songIds)));
    }

    boolean isLiked(Song song) {
        return likedIds.contains(song.getId());
    }

    boolean isDisliked(Song song) {
        return dislikedIds.contains(song.getId());
    }

    long likesCount(Song song) {
        return likesCounts.getOrDefault(song.getId(), 0L);
    }

    long dislikesCount(Song song) {
        return dislikesCounts.getOrDefault(song.getId(), 0L);
    }

    private static Map<UUID, Long> toCountMap(List<SongIdCountProjection> rows) {
        Map<UUID, Long> map = new HashMap<>();
        for (SongIdCountProjection row : rows) {
            map.put(row.getSongId(), row.getCnt());
        }
        return map;
    }
}
