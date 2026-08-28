package com.vjiki.music.mapper;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.entity.Playlist;

public final class PlaylistMapper {

    private PlaylistMapper() {
    }

    public static PlaylistResponse toResponse(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .userId(playlist.getUser() == null ? null : playlist.getUser().getId())
                .userName(playlist.getUser() == null ? null : playlist.getUser().getEmail())
                .userNickname(playlist.getUser() == null ? null : playlist.getUser().getNickname())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .coverUrl(playlist.getCoverUrl())
                .type(playlist.getType())
                .isPublic(playlist.getIsPublic())
                .createdAt(playlist.getCreatedAt())
                .modifiedAt(playlist.getModifiedAt())
                .build();
    }
}
