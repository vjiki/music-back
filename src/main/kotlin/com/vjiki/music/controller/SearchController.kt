package com.vjiki.music.controller

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.service.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/search")
class SearchController(
    private val searchService: SearchService
) {
    /**
     * Search songs by keyword across:
     * - songs.title
     * - songs.artists (jsonb default array)
     * - tag.name (via track_tag)
     *
     * Example:
     * GET /api/v1/search/songs/{userId}?q=chill&limit=20&cursor=...
     */
    @GetMapping("/songs/{userId}")
    fun searchSongs(
        @PathVariable userId: UUID,
        @RequestParam q: String,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) cursor: String?
    ): ResponseEntity<CursorPageResponse<SongResponse>> {
        val result = searchService.searchSongs(userId = userId, q = q, limit = limit, cursor = cursor)
        return ResponseEntity.ok(result)
    }
}


