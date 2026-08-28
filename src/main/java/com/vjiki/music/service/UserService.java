package com.vjiki.music.service;

import java.util.UUID;

import com.vjiki.music.dto.AuthExistsResponse;
import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.RegisterRequest;
import com.vjiki.music.dto.UserResponse;

public interface UserService {

    UserResponse getUserById(UUID userId);

    AuthResponse authenticate(AuthRequest authRequest);

    AuthResponse authenticateFirebase(String idToken);

    AuthResponse registerIfNotExists(RegisterRequest request);

    AuthExistsResponse userExistsByEmail(String email, String provider);

    default AuthExistsResponse userExistsByEmail(String email) {
        return userExistsByEmail(email, null);
    }
}
