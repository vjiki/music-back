package com.vjiki.music.mapper;

import com.vjiki.music.dto.StoryResponse;
import com.vjiki.music.entity.Story;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class StoryMapperImpl implements StoryMapper {

    @Override
    public StoryResponse toResponse(Story story) {
        OffsetDateTime now = OffsetDateTime.now();
        boolean isExpired = Optional.ofNullable(story.getExpiresAt())
                .map(expiresAt -> expiresAt.isBefore(now))
                .orElse(false);
        
        return new StoryResponse(
                story.getId(),
                story.getUserId(),
                Optional.ofNullable(story.getUser())
                        .map(user -> user.getNickname())
                        .orElse(null),
                Optional.ofNullable(story.getUser())
                        .map(user -> user.getAvatarUrl())
                        .orElse(null),
                story.getImageUrl(),
                story.getPreviewUrl(),
                story.getStoryType() != null ? story.getStoryType().name() : null,
                story.getSongId(),
                Optional.ofNullable(story.getSong())
                        .map(song -> song.getTitle())
                        .orElse(null),
                Optional.ofNullable(story.getSong())
                        .map(song -> extractArtist(song.getArtists()))
                        .orElse(null),
                story.getCaption(),
                story.getLocation(),
                story.getViewsCount(),
                story.getCreatedAt(),
                story.getExpiresAt(),
                isExpired
        );
    }

    private String extractArtist(java.util.Map<String, java.util.List<String>> artists) {
        return Optional.ofNullable(artists)
                .map(map -> map.get("default"))
                .filter(list -> !list.isEmpty())
                .map(list -> list.getFirst())
                .orElse(null);
    }
}

