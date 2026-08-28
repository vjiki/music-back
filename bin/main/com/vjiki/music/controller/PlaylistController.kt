package com.vjiki.music.controller

import com.vjiki.music.dto.PlaylistResponse
import com.vjiki.music.dto.PlaylistWithSongsResponse
import com.vjiki.music.service.PlaylistService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/playlists")
class PlaylistController(
    private val playlistService: PlaylistService
) {

    @GetMapping("/user/{userId}")
    fun getPlaylistsByUserId(@PathVariable userId: UUID): ResponseEntity<List<PlaylistResponse>> {
        val playlists = playlistService.getPlaylistsByUserId(userId)
        return ResponseEntity.ok(playlists)
    }

    @GetMapping("/{playlistId}")
    fun getPlaylistWithSongs(@PathVariable playlistId: UUID): ResponseEntity<PlaylistWithSongsResponse> {
        val playlist = playlistService.getPlaylistWithSongs(playlistId)
        return ResponseEntity.ok(playlist)
    }
}

