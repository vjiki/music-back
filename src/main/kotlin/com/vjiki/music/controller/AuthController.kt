package com.vjiki.music.controller

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.dto.AuthExistsResponse
import com.vjiki.music.dto.RegisterRequest
import com.vjiki.music.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/authenticate")
    @CrossOrigin(origins = ["*"])
    fun authenticate(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val authResponse = userService.authenticate(authRequest)

        return if (authResponse.authenticated) {
            ResponseEntity.ok(authResponse)
        } else {
            ResponseEntity.status(401).body(authResponse)
        }
    }

    /**
     * Register a LOCAL user if it doesn't exist (by email).
     * If user exists and password matches, returns existing userId.
     */
    @PostMapping("/register")
    @CrossOrigin(origins = ["*"])
    fun registerIfNotExists(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = userService.registerIfNotExists(request)
        return ResponseEntity.ok(response)
    }

    /**
     * Check whether user exists by email (for FE flows).
     * Example: GET /api/v1/auth/exists?email=user@example.com
     * Provider-specific check: GET /api/v1/auth/exists?email=user@example.com&provider=GOOGLE
     */
    @GetMapping("/exists")
    @CrossOrigin(origins = ["*"])
    fun userExists(
        @RequestParam email: String,
        @RequestParam(required = false) provider: String?
    ): ResponseEntity<AuthExistsResponse> {
        val response = userService.userExistsByEmail(email, provider)
        return ResponseEntity.ok(response)
    }
}

