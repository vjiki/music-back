package com.vjiki.music.controller

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
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
}

