package com.vjiki.music.service;

import com.vjiki.music.dto.SongLikeResponse;

import java.util.UUID;

public interface SongLikeService {
    void likeSong(UUID userId, UUID songId);
    void dislikeSong(UUID userId, UUID songId);
    SongLikeResponse getLikeDislikeInfo(UUID userId, UUID songId);
}

