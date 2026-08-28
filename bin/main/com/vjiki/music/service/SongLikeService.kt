package com.vjiki.music.service

import com.vjiki.music.dto.SongLikeResponse
import java.util.UUID

interface SongLikeService {
    fun likeSong(userId: UUID, songId: UUID)
    fun dislikeSong(userId: UUID, songId: UUID)
    fun getLikeDislikeInfo(userId: UUID, songId: UUID): SongLikeResponse
}

