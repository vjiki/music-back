package com.vjiki.music.mapper;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.entity.Song;

public interface SongMapper {
    SongResponse toResponse(Song song);
}

