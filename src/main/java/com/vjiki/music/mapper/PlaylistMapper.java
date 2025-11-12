package com.vjiki.music.mapper;

import com.vjiki.music.dto.PlaylistResponse;
import com.vjiki.music.entity.Playlist;

public interface PlaylistMapper {
    PlaylistResponse toResponse(Playlist playlist);
}

