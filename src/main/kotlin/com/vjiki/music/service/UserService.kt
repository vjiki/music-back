package com.vjiki.music.service

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.dto.AuthExistsResponse
import com.vjiki.music.dto.RegisterRequest
import com.vjiki.music.dto.UserResponse
import java.util.UUID

interface UserService {
    fun getUserById(userId: UUID): UserResponse
    fun authenticate(authRequest: AuthRequest): AuthResponse
    fun authenticateFirebase(idToken: String): AuthResponse
    fun registerIfNotExists(request: RegisterRequest): AuthResponse
    fun userExistsByEmail(email: String, provider: String? = null): AuthExistsResponse
}

