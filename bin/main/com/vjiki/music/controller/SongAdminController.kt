package com.vjiki.music.controller

import com.vjiki.music.dto.CreateSongRequest
import com.vjiki.music.dto.SongResponse
import com.vjiki.music.security.UserPrincipal
import com.vjiki.music.service.SongAdminService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/songs")
class SongAdminController(
    private val songAdminService: SongAdminService
) {
    @PostMapping
    fun createSong(@RequestBody request: CreateSongRequest): ResponseEntity<SongResponse> {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        val createdBy = when (principal) {
            is UserPrincipal -> principal.userId.toString()
            else -> "system"
        }
        val created = songAdminService.createSong(request, createdBy = createdBy)
        return ResponseEntity.ok(created)
    }
}


