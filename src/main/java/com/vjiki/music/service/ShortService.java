package com.vjiki.music.service;

import java.util.List;
import java.util.UUID;

import com.vjiki.music.dto.ShortResponse;
import com.vjiki.music.pagination.CursorPageResponse;

public interface ShortService {

    List<ShortResponse> getShorts(UUID userId);

    CursorPageResponse<ShortResponse> getShortsPage(UUID userId, int limit, String cursor);
}
