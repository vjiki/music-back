package com.vjiki.music.service;

import com.vjiki.music.dto.SongResponse;

import java.util.List;

public interface SongService {
    List<SongResponse> getSongs(String userId);
}

