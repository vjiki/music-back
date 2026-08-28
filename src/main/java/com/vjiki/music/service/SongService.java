package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.pagination.CursorPageResponse;

public interface SongService {

    List<SongResponse> getSongs(UUID userId);

    CursorPageResponse<SongResponse> getSongsPage(UUID userId, int limit, String cursor);
}
