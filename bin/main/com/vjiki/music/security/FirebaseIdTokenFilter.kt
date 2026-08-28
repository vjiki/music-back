package com.vjiki.music.security

import com.google.firebase.auth.FirebaseAuth
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import com.vjiki.music.repository.UserRoleRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class FirebaseIdTokenFilter(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val playlistRepository: PlaylistRepository
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI ?: ""
        // Public endpoints
        return path.startsWith("/healthz") ||
            path.startsWith("/actuator") ||
            path.startsWith("/api/v1/auth")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = header
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")
            ?.trim()

        if (token.isNullOrBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val decoded = firebaseAuth.verifyIdToken(token)
            val email = decoded.email?.trim()?.lowercase()
            val uid = decoded.uid

            if (email.isNullOrBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Firebase token has no email")
                return
            }

            val nickname = (decoded.name ?: email.substringBefore("@")).take(100).ifBlank { "user" }
            val avatarUrl = decoded.picture?.trim()?.takeIf { it.isNotBlank() }

            // Upsert user as GOOGLE (idempotent). Password remains NULL.
            val userId = userRepository.upsertUserReturnId(
                email = email,
                passwordHash = null,
                provider = AuthProvider.GOOGLE.name,
                providerId = uid,
                nickname = nickname,
                avatarUrl = avatarUrl
            )

            // Ensure default role + playlists exist
            userRoleRepository.insertRoleIfMissing(userId, "USER")
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

            val roles = userRoleRepository.findRolesByUserId(userId)
            val authorities = roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }

            val principal = UserPrincipal(userId = userId, email = email, firebaseUid = uid)
            val auth = UsernamePasswordAuthenticationToken(principal, null, authorities)
            SecurityContextHolder.getContext().authentication = auth

            filterChain.doFilter(request, response)
        } catch (_: Exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token")
        }
    }
}


