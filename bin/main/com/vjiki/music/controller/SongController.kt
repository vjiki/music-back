package com.vjiki.music.controller

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.pagination.CursorPageResponse
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

    /**
     * Cursor-based pagination for FE (music-ios / music-ui).
     *
     * Example:
     * GET /api/v1/songs/{userId}/page?limit=20
     * GET /api/v1/songs/{userId}/page?limit=20&cursor=<nextCursor>
     */
    @GetMapping("/{userId}/page")
    fun getSongsPage(
        @PathVariable userId: UUID,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) cursor: String?
    ): ResponseEntity<CursorPageResponse<SongResponse>> {
        val page = songService.getSongsPage(userId = userId, limit = limit, cursor = cursor)
        return ResponseEntity.ok(page)
    }
}

