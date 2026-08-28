package com.vjiki.music.mapper;

import java.time.OffsetDateTime;

import com.vjiki.music.dto.StoryResponse;
import com.vjiki.music.entity.Story;

public final class StoryMapper {

    private StoryMapper() {
    }

    public static StoryResponse toResponse(Story story) {
        OffsetDateTime now = OffsetDateTime.now();
        boolean isExpired = story.getExpiresAt() != null && story.getExpiresAt().isBefore(now);

        return StoryResponse.builder()
                .id(story.getId())
                .userId(story.getUserId())
                .userNickname(story.getUser() == null ? null : story.getUser().getNickname())
                .userAvatarUrl(story.getUser() == null ? null : story.getUser().getAvatarUrl())
                .imageUrl(story.getImageUrl())
                .previewUrl(story.getPreviewUrl())
                .storyType(story.getStoryType() == null ? null : story.getStoryType().name())
                .songId(story.getSongId())
                .songTitle(story.getSong() == null ? null : story.getSong().getTitle())
                .songArtist(story.getSong() == null ? null : SongMapper.extractArtist(story.getSong().getArtists()))
                .caption(story.getCaption())
                .location(story.getLocation())
                .viewsCount(story.getViewsCount())
                .createdAt(story.getCreatedAt())
                .expiresAt(story.getExpiresAt())
                .isExpired(isExpired)
                .build();
    }
}
