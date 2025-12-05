package com.vjiki.music.controller

import com.vjiki.music.dto.FollowerResponse
import com.vjiki.music.service.FollowerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/followers")
class FollowerController(
    private val followerService: FollowerService
) {

    @GetMapping("/{userId}")
    fun getFollowersByUserId(@PathVariable userId: UUID): ResponseEntity<List<FollowerResponse>> {
        val followers = followerService.getFollowersByUserId(userId)
        return ResponseEntity.ok(followers)
    }
}

