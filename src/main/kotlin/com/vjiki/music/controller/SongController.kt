package com.vjiki.music.controller

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.service.SongService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/songs")
class SongController(
    private val songService: SongService
) {

    @GetMapping("/{userId}")
    fun getSongs(@PathVariable userId: UUID): ResponseEntity<List<SongResponse>> {
        val songs = songService.getSongs(userId)
        return ResponseEntity.ok(songs)
    }
}

