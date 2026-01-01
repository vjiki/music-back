package com.vjiki.music.service

import com.google.firebase.auth.FirebaseAuth
import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.dto.AuthExistsResponse
import com.vjiki.music.dto.RegisterRequest
import com.vjiki.music.dto.UserResponse
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.mapper.UserMapper.toResponse
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import com.vjiki.music.repository.UserRoleRepository
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
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
    private val passwordEncoder: PasswordEncoder,
    private val firebaseAuthProvider: ObjectProvider<FirebaseAuth>,
    @Value("\${auth.local.enabled:true}")
    private val localAuthEnabled: Boolean
) : UserService {

    override fun getUserById(userId: UUID): UserResponse {
        return userRepository.findById(userId)
            .map { it.toResponse() }
            .orElseThrow { RuntimeException("User not found with id: $userId") }
    }

    override fun authenticate(authRequest: AuthRequest): AuthResponse {
        if (!localAuthEnabled) {
            // In production deployments we only allow Firebase Auth.
            return AuthResponse(false, null, "Local authentication is disabled")
        }

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

    override fun authenticateFirebase(idToken: String): AuthResponse {
        val fa = firebaseAuthProvider.ifAvailable ?: return AuthResponse(false, null, "Firebase auth is not enabled")
        return try {
            val decoded = fa.verifyIdToken(idToken)
            val email = decoded.email?.trim()?.lowercase()
                ?: return AuthResponse(false, null, "Firebase token has no email")

            val nickname = (decoded.name ?: email.substringBefore("@")).take(100).ifBlank { "user" }
            val avatarUrl = decoded.picture?.trim()?.takeIf { it.isNotBlank() }

            val userId = userRepository.upsertUserReturnId(
                email = email,
                passwordHash = null,
                provider = AuthProvider.GOOGLE.name,
                providerId = decoded.uid,
                nickname = nickname,
                avatarUrl = avatarUrl
            )

            userRoleRepository.insertRoleIfMissing(userId, "USER")
            ensureDefaultPlaylists(userId)

            AuthResponse(true, userId, "Authentication successful")
        } catch (_: Exception) {
            AuthResponse(false, null, "Invalid Firebase token")
        }
    }

    @Transactional
    override fun registerIfNotExists(request: RegisterRequest): AuthResponse {
        // Local users are dev-only. In production, require Firebase flow (/auth/firebase) for provisioning.
        if (!localAuthEnabled) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Local registration is disabled")
        }

        val email = request.email.trim().lowercase()
        val password = request.password?.trim()?.takeIf { it.isNotBlank() }
        val provider = runCatching { AuthProvider.valueOf(request.provider?.trim()?.uppercase() ?: AuthProvider.LOCAL.name) }
            .getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid provider") }
        val providerId = request.providerId?.trim()?.takeIf { it.isNotBlank() }

        if (email.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required")
        if (provider == AuthProvider.LOCAL && password != null && password.length < 6) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters")
        }

        // When local registration is enabled, we only support LOCAL here.
        // OAuth users must be provisioned via verified Firebase token (/auth/firebase) to avoid spoofing.
        if (provider != AuthProvider.LOCAL) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth registration is disabled for this endpoint. Use /api/v1/auth/firebase")
        }

        // Native SELECT (no Hibernate merge/persist involved)
        val existing = userRepository.findRegisterInfoByEmailNative(email)
        if (existing != null) {
            // Silent-idempotent:
            // - Always return 200 + userId if email exists
            // - If password is provided and LOCAL user has no password, set it (native UPDATE)
            if (provider == AuthProvider.LOCAL && password != null && existing.provider == AuthProvider.LOCAL.name && existing.passwordHash.isNullOrBlank()) {
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

        val savedId = userRepository.upsertUserReturnId(
            email = email,
            passwordHash = if (provider == AuthProvider.LOCAL) password?.let { passwordEncoder.encode(it) } else null,
            provider = provider.name,
            providerId = if (provider == AuthProvider.LOCAL) null else providerId,
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

    override fun userExistsByEmail(email: String, provider: String?): AuthExistsResponse {
        val normalized = email.trim().lowercase()
        if (normalized.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required")

        val providerFilter = provider?.trim()?.takeIf { it.isNotBlank() }?.uppercase()
        val info = userRepository.findRegisterInfoByEmailNative(normalized)
        return if (info != null && (providerFilter == null || info.provider.uppercase() == providerFilter)) {
            AuthExistsResponse(exists = true, userId = info.id, provider = info.provider)
        } else {
            AuthExistsResponse(exists = false, userId = null, provider = null)
        }
    }
}

