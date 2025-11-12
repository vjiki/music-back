package com.vjiki.music.mapper;

import com.vjiki.music.dto.PlaylistSongResponse;
import com.vjiki.music.entity.PlaylistSong;

public interface PlaylistSongMapper {
    PlaylistSongResponse toResponse(PlaylistSong playlistSong);
}

