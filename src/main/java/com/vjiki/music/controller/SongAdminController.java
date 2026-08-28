package com.vjiki.music.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.CreateSongRequest;
import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.security.UserPrincipal;
import com.vjiki.music.service.SongAdminService;

@RestController
@RequestMapping("/api/v1/admin/songs")
public class SongAdminController {

    private final SongAdminService songAdminService;

    public SongAdminController(SongAdminService songAdminService) {
        this.songAdminService = songAdminService;
    }

    @PostMapping
    public ResponseEntity<SongResponse> createSong(@RequestBody CreateSongRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        String createdBy = principal instanceof UserPrincipal userPrincipal
                ? userPrincipal.userId().toString()
                : "system";

        return ResponseEntity.ok(songAdminService.createSong(request, createdBy));
    }
}
