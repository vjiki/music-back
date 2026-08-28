package com.vjiki.music.service;

import java.util.UUID;

import com.vjiki.music.dto.SongLikeResponse;

public interface SongLikeService {

    void likeSong(UUID userId, UUID songId);

    void dislikeSong(UUID userId, UUID songId);

    SongLikeResponse getLikeDislikeInfo(UUID userId, UUID songId);
}
