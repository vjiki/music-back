package com.vjiki.music.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.FollowerResponse;
import com.vjiki.music.service.FollowerService;

@RestController
@RequestMapping("/api/v1/followers")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FollowerResponse>> getFollowersByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(followerService.getFollowersByUserId(userId));
    }
}
