package com.vjiki.music.mapper;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.entity.Playlist;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlaylistMapperImpl implements PlaylistMapper {

    @Override
    public PlaylistResponse toResponse(Playlist playlist) {
        return new PlaylistResponse(
                playlist.getId(),
                Optional.ofNullable(playlist.getUser())
                        .map(user -> user.getId())
                        .orElse(null),
                Optional.ofNullable(playlist.getUser())
                        .map(user -> user.getEmail())
                        .orElse(null),
                Optional.ofNullable(playlist.getUser())
                        .map(user -> user.getNickname())
                        .orElse(null),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getCoverUrl(),
                playlist.getType(),
                playlist.getIsPublic(),
                playlist.getCreatedAt(),
                playlist.getModifiedAt()
        );
    }
}

