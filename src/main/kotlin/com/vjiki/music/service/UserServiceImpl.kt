package com.vjiki.music.service

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.dto.UserResponse
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.mapper.UserMapper.toResponse
import com.vjiki.music.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun getUserById(userId: UUID): UserResponse {
        return userRepository.findById(userId)
            .map { it.toResponse() }
            .orElseThrow { RuntimeException("User not found with id: $userId") }
    }

    override fun authenticate(authRequest: AuthRequest): AuthResponse {
        val user = userRepository.findByEmail(authRequest.email)
            .orElse(null) ?: return AuthResponse(false, null, "Invalid email or password")

        if (user.isActive == false) {
            return AuthResponse(false, null, "User account is inactive")
        }

        if (user.provider == AuthProvider.LOCAL) {
            if (user.passwordHash.isNullOrEmpty()) {
                return AuthResponse(false, null, "Password not set for this user")
            }

            if (!passwordEncoder.matches(authRequest.password, user.passwordHash)) {
                return AuthResponse(false, null, "Invalid email or password")
            }
        } else {
            return AuthResponse(false, null, "This account uses OAuth authentication")
        }

        return AuthResponse(true, user.id, "Authentication successful")
    }
}

