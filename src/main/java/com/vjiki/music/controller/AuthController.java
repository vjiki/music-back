package com.vjiki.music.controller;

import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = userService.authenticate(authRequest);
        
        if (authResponse.getAuthenticated()) {
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(401).body(authResponse);
        }
    }
}

