package com.vjiki.music.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjiki.music.dto.AuthExistsResponse;
import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.FirebaseAuthRequest;
import com.vjiki.music.dto.RegisterRequest;
import com.vjiki.music.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = userService.authenticate(authRequest);

        return Boolean.TRUE.equals(authResponse.getAuthenticated())
                ? ResponseEntity.ok(authResponse)
                : ResponseEntity.status(401).body(authResponse);
    }

    /**
     * Firebase Auth login: verifies Firebase ID token and returns userId.
     * Requires FIREBASE_ENABLED=true and service account credentials env vars.
     */
    @PostMapping("/firebase")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse> authenticateFirebase(@RequestBody FirebaseAuthRequest request) {
        AuthResponse authResponse = userService.authenticateFirebase(request.getIdToken());

        return Boolean.TRUE.equals(authResponse.getAuthenticated())
                ? ResponseEntity.ok(authResponse)
                : ResponseEntity.status(401).body(authResponse);
    }

    /**
     * Register a LOCAL user if it doesn't exist (by email).
     * If user exists and password matches, returns existing userId.
     */
    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse> registerIfNotExists(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerIfNotExists(request));
    }

    /**
     * Check whether user exists by email (for FE flows).
     * Example: GET /api/v1/auth/exists?email=user@example.com
     * Provider-specific check: GET /api/v1/auth/exists?email=user@example.com&provider=GOOGLE
     */
    @GetMapping("/exists")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthExistsResponse> userExists(
            @RequestParam String email,
            @RequestParam(required = false) String provider) {
        return ResponseEntity.ok(userService.userExistsByEmail(email, provider));
    }
}
