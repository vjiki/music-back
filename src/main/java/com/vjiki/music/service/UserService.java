package com.vjiki.music.service;

import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID userId);
    AuthResponse authenticate(AuthRequest authRequest);
}

