package com.vjiki.music.security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.repository.UserRoleRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FirebaseIdTokenFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PlaylistRepository playlistRepository;

    public FirebaseIdTokenFilter(FirebaseAuth firebaseAuth,
                                 UserRepository userRepository,
                                 UserRoleRepository userRoleRepository,
                                 PlaylistRepository playlistRepository) {
        this.firebaseAuth = firebaseAuth;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.playlistRepository = playlistRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI() == null ? "" : request.getRequestURI();
        // Public endpoints
        return path.startsWith("/healthz")
                || path.startsWith("/actuator")
                || path.startsWith("/api/v1/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring("Bearer ".length()).trim();
        }

        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(token);
            String email = decoded.getEmail() == null ? null : decoded.getEmail().trim().toLowerCase(Locale.ROOT);
            String uid = decoded.getUid();

            if (email == null || email.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Firebase token has no email");
                return;
            }

            String rawNickname = decoded.getName() != null ? decoded.getName() : email.substring(0, email.indexOf('@'));
            String nickname = rawNickname.length() > 100 ? rawNickname.substring(0, 100) : rawNickname;
            if (nickname.isBlank()) {
                nickname = "user";
            }

            String avatarUrl = null;
            if (decoded.getPicture() != null && !decoded.getPicture().trim().isBlank()) {
                avatarUrl = decoded.getPicture().trim();
            }

            // Upsert user as GOOGLE (idempotent). Password remains NULL.
            UUID userId = userRepository.upsertUserReturnId(
                    email,
                    null,
                    AuthProvider.GOOGLE.name(),
                    uid,
                    nickname,
                    avatarUrl);

            // Ensure default role + playlists exist
            userRoleRepository.insertRoleIfMissing(userId, "USER");
            playlistRepository.insertPlaylistIfMissing(
                    userId,
                    "DEFAULT_LIKES",
                    "Default liked songs playlist",
                    null,
                    "DEFAULT",
                    true);
            playlistRepository.insertPlaylistIfMissing(
                    userId,
                    "DEFAULT_DISLIKES",
                    "Default disliked songs playlist",
                    null,
                    "DEFAULT",
                    true);

            List<String> roles = userRoleRepository.findRolesByUserId(userId);
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)))
                    .toList();

            UserPrincipal principal = new UserPrincipal(userId, email, uid);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token");
        }
    }
}
