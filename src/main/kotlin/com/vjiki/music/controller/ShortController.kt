package com.vjiki.music.controller

import com.vjiki.music.dto.ShortResponse
import com.vjiki.music.service.ShortService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/shorts")
class ShortController(
    private val shortService: ShortService
) {

    @GetMapping("/{userId}")
    fun getShorts(@PathVariable userId: UUID): ResponseEntity<List<ShortResponse>> {
        val shorts = shortService.getShorts(userId)
        return ResponseEntity.ok(shorts)
    }
}

