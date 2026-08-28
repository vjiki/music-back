package com.vjiki.music.mapper;

import com.vjiki.music.dto.ShortResponse;
import com.vjiki.music.entity.Song;

public final class ShortMapper {

    private ShortMapper() {
    }

    public static ShortResponse toShortResponse(Song song) {
        return toShortResponse(song, false, false, song.getLikesCount(), song.getDislikesCount());
    }

    public static ShortResponse toShortResponse(Song song, boolean isLiked, boolean isDisliked) {
        return toShortResponse(song, isLiked, isDisliked, song.getLikesCount(), song.getDislikesCount());
    }

    public static ShortResponse toShortResponse(Song song,
                                                boolean isLiked,
                                                boolean isDisliked,
                                                Long likesCount,
                                                Long dislikesCount) {
        return ShortResponse.builder()
                .id(song.getId() == null ? null : song.getId().toString())
                .artist(SongMapper.extractArtist(song.getArtists()))
                .audioUrl(SongMapper.extractUrl(song.getAudioUrls()))
                .cover(SongMapper.extractUrl(song.getCoverUrls()))
                .title(song.getTitle())
                .videoUrl(SongMapper.extractUrl(song.getVideoUrls()))
                .type(song.getType())
                .isLiked(isLiked)
                .isDisliked(isDisliked)
                .likesCount(likesCount == null ? 0L : likesCount)
                .dislikesCount(dislikesCount == null ? 0L : dislikesCount)
                .build();
    }
}
