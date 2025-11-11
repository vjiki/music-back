package com.vjiki.music.controller;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.service.FollowerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/followers")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FollowerResponse>> getFollowersByUserId(@PathVariable UUID userId) {
        List<FollowerResponse> followers = followerService.getFollowersByUserId(userId);
        return ResponseEntity.ok(followers);
    }
}

