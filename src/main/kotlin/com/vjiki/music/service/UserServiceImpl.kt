package com.vjiki.music.service

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.dto.AuthExistsResponse
import com.vjiki.music.dto.RegisterRequest
import com.vjiki.music.dto.UserResponse
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import com.vjiki.music.mapper.UserMapper.toResponse
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import com.vjiki.music.repository.UserRoleRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val playlistRepository: PlaylistRepository,
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

    @Transactional
    override fun registerIfNotExists(request: RegisterRequest): AuthResponse {
        val email = request.email.trim().lowercase()
        val password = request.password?.trim()?.takeIf { it.isNotBlank() }

        if (email.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required")
        if (password != null && password.length < 6) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters")
        }

        // Native SELECT (no Hibernate merge/persist involved)
        val existing = userRepository.findRegisterInfoByEmailNative(email)
        if (existing != null) {
            // Silent-idempotent:
            // - Always return 200 + userId if email exists
            // - If password is provided and LOCAL user has no password, set it (native UPDATE)
            if (password != null && existing.provider == AuthProvider.LOCAL.name && existing.passwordHash.isNullOrBlank()) {
                userRepository.setPasswordIfMissing(existing.id, passwordEncoder.encode(password))
                userRoleRepository.insertRoleIfMissing(existing.id, "USER")
                ensureDefaultPlaylists(existing.id)
                return AuthResponse(true, existing.id, "Registration successful")
            }

            userRoleRepository.insertRoleIfMissing(existing.id, "USER")
            ensureDefaultPlaylists(existing.id)
            return AuthResponse(true, existing.id, "User already exists")
        }

        val nickname = request.nickname
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.take(100)
            ?: email.substringBefore("@").take(100).ifBlank { "user" }

        val savedId = userRepository.upsertLocalUserReturnId(
            email = email,
            passwordHash = password?.let { passwordEncoder.encode(it) },
            nickname = nickname,
            avatarUrl = request.avatarUrl?.trim()?.takeIf { it.isNotBlank() }
        )

        // Default role for new users
        userRoleRepository.insertRoleIfMissing(savedId, "USER")
        ensureDefaultPlaylists(savedId)

        return AuthResponse(true, savedId, "Registration successful")
    }

    private fun ensureDefaultPlaylists(userId: UUID) {
        // Names per your examples; playlists.type is "DEFAULT" and they are public.
        playlistRepository.insertPlaylistIfMissing(
            userId = userId,
            name = "DEFAULT_LIKES",
            description = "Default liked songs playlist",
            coverUrl = null,
            type = "DEFAULT",
            isPublic = true
        )
        playlistRepository.insertPlaylistIfMissing(
            userId = userId,
            name = "DEFAULT_DISLIKES",
            description = "Default disliked songs playlist",
            coverUrl = null,
            type = "DEFAULT",
            isPublic = true
        )
    }

    override fun userExistsByEmail(email: String): AuthExistsResponse {
        val normalized = email.trim().lowercase()
        if (normalized.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required")

        val user = userRepository.findByEmail(normalized).orElse(null)
        return if (user != null) {
            AuthExistsResponse(exists = true, userId = user.id)
        } else {
            AuthExistsResponse(exists = false, userId = null)
        }
    }
}

