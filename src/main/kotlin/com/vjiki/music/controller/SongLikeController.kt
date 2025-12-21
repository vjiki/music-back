package com.vjiki.music.controller

import com.vjiki.music.dto.SongLikeRequest
import com.vjiki.music.service.SongLikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/song-likes")
class SongLikeController(
    private val songLikeService: SongLikeService
) {

    @PostMapping("/like")
    fun likeSong(@RequestBody request: SongLikeRequest): ResponseEntity<Void> {
        songLikeService.likeSong(request.userId, request.songId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/dislike")
    fun dislikeSong(@RequestBody request: SongLikeRequest): ResponseEntity<Void> {
        songLikeService.dislikeSong(request.userId, request.songId)
        return ResponseEntity.ok().build()
    }
}

