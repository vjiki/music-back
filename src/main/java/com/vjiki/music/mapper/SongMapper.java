package com.vjiki.music.mapper;

import java.util.List;
import java.util.Map;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.entity.Song;

public final class SongMapper {

    private SongMapper() {
    }

    public static SongResponse toResponse(Song song) {
        return toResponse(song, false, false, song.getLikesCount(), song.getDislikesCount());
    }

    public static SongResponse toResponse(Song song, boolean isLiked, boolean isDisliked) {
        return toResponse(song, isLiked, isDisliked, song.getLikesCount(), song.getDislikesCount());
    }

    public static SongResponse toResponse(Song song,
                                          boolean isLiked,
                                          boolean isDisliked,
                                          Long likesCount,
                                          Long dislikesCount) {
        return SongResponse.builder()
                .id(song.getId() == null ? null : song.getId().toString())
                .artist(extractArtist(song.getArtists()))
                .audioUrl(extractUrl(song.getAudioUrls()))
                .cover(extractUrl(song.getCoverUrls()))
                .title(song.getTitle())
                .videoUrl(extractUrl(song.getVideoUrls()))
                .isLiked(isLiked)
                .isDisliked(isDisliked)
                .likesCount(likesCount == null ? 0L : likesCount)
                .dislikesCount(dislikesCount == null ? 0L : dislikesCount)
                .build();
    }

    static String extractArtist(Map<String, List<String>> artists) {
        if (artists == null) {
            return null;
        }
        List<String> values = artists.get("default");
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.getFirst();
    }

    static String extractUrl(Map<String, String> urls) {
        return urls == null ? null : urls.get("default");
    }
}
