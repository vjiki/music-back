package com.vjiki.music.service;

import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.vjiki.music.dto.AuthExistsResponse;
import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.RegisterRequest;
import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.mapper.UserMapper;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.repository.UserRoleRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_NICKNAME_LENGTH = 100;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<FirebaseAuth> firebaseAuthProvider;
    private final boolean localAuthEnabled;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PlaylistRepository playlistRepository,
                           PasswordEncoder passwordEncoder,
                           ObjectProvider<FirebaseAuth> firebaseAuthProvider,
                           @Value("${auth.local.enabled:true}") boolean localAuthEnabled) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.playlistRepository = playlistRepository;
        this.passwordEncoder = passwordEncoder;
        this.firebaseAuthProvider = firebaseAuthProvider;
        this.localAuthEnabled = localAuthEnabled;
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        if (!localAuthEnabled) {
            // In production deployments we only allow Firebase Auth.
            return new AuthResponse(false, null, "Local authentication is disabled");
        }

        var user = userRepository.findByEmail(authRequest.getEmail()).orElse(null);
        if (user == null) {
            return new AuthResponse(false, null, "Invalid email or password");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            return new AuthResponse(false, null, "User account is inactive");
        }

        if (user.getProvider() == AuthProvider.LOCAL) {
            if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                return new AuthResponse(false, null, "Password not set for this user");
            }
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
                return new AuthResponse(false, null, "Invalid email or password");
            }
        } else {
            return new AuthResponse(false, null, "This account uses OAuth authentication");
        }

        return new AuthResponse(true, user.getId(), "Authentication successful");
    }

    @Override
    public AuthResponse authenticateFirebase(String idToken) {
        FirebaseAuth firebaseAuth = firebaseAuthProvider.getIfAvailable();
        if (firebaseAuth == null) {
            return new AuthResponse(false, null, "Firebase auth is not enabled");
        }

        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);
            String email = decoded.getEmail() == null ? null : decoded.getEmail().trim().toLowerCase(Locale.ROOT);
            if (email == null) {
                return new AuthResponse(false, null, "Firebase token has no email");
            }

            String nickname = resolveNickname(decoded.getName(), email);
            String avatarUrl = trimToNull(decoded.getPicture());

            UUID userId = userRepository.upsertUserReturnId(
                    email,
                    null,
                    AuthProvider.GOOGLE.name(),
                    decoded.getUid(),
                    nickname,
                    avatarUrl);

            userRoleRepository.insertRoleIfMissing(userId, "USER");
            ensureDefaultPlaylists(userId);

            return new AuthResponse(true, userId, "Authentication successful");
        } catch (Exception e) {
            return new AuthResponse(false, null, "Invalid Firebase token");
        }
    }

    @Override
    @Transactional
    public AuthResponse registerIfNotExists(RegisterRequest request) {
        // Local users are dev-only. In production, require Firebase flow (/auth/firebase) for provisioning.
        if (!localAuthEnabled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Local registration is disabled");
        }

        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase(Locale.ROOT);
        String password = trimToNull(request.getPassword());

        AuthProvider provider;
        try {
            String rawProvider = trimToNull(request.getProvider());
            provider = rawProvider == null
                    ? AuthProvider.LOCAL
                    : AuthProvider.valueOf(rawProvider.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid provider");
        }
        String providerId = trimToNull(request.getProviderId());

        if (email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (provider == AuthProvider.LOCAL && password != null && password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }

        // When local registration is enabled, we only support LOCAL here.
        // OAuth users must be provisioned via verified Firebase token (/auth/firebase) to avoid spoofing.
        if (provider != AuthProvider.LOCAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "OAuth registration is disabled for this endpoint. Use /api/v1/auth/firebase");
        }

        // Native SELECT (no Hibernate merge/persist involved)
        UserRepository.RegisterInfoProjection existing = userRepository.findRegisterInfoByEmailNative(email);
        if (existing != null) {
            // Silent-idempotent:
            // - Always return 200 + userId if email exists
            // - If password is provided and LOCAL user has no password, set it (native UPDATE)
            boolean canSetPassword = password != null
                    && AuthProvider.LOCAL.name().equals(existing.getProvider())
                    && (existing.getPasswordHash() == null || existing.getPasswordHash().isBlank());

            if (canSetPassword) {
                userRepository.setPasswordIfMissing(existing.getId(), passwordEncoder.encode(password));
                userRoleRepository.insertRoleIfMissing(existing.getId(), "USER");
                ensureDefaultPlaylists(existing.getId());
                return new AuthResponse(true, existing.getId(), "Registration successful");
            }

            userRoleRepository.insertRoleIfMissing(existing.getId(), "USER");
            ensureDefaultPlaylists(existing.getId());
            return new AuthResponse(true, existing.getId(), "User already exists");
        }

        String nickname = resolveNickname(request.getNickname(), email);

        UUID savedId = userRepository.upsertUserReturnId(
                email,
                password == null ? null : passwordEncoder.encode(password),
                provider.name(),
                null,
                nickname,
                trimToNull(request.getAvatarUrl()));

        // Default role for new users
        userRoleRepository.insertRoleIfMissing(savedId, "USER");
        ensureDefaultPlaylists(savedId);

        return new AuthResponse(true, savedId, "Registration successful");
    }

    @Override
    public AuthExistsResponse userExistsByEmail(String email, String provider) {
        String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        String providerFilter = trimToNull(provider);
        if (providerFilter != null) {
            providerFilter = providerFilter.toUpperCase(Locale.ROOT);
        }

        UserRepository.RegisterInfoProjection info = userRepository.findRegisterInfoByEmailNative(normalized);
        boolean matches = info != null
                && (providerFilter == null || info.getProvider().toUpperCase(Locale.ROOT).equals(providerFilter));

        return matches
                ? new AuthExistsResponse(true, info.getId(), info.getProvider())
                : new AuthExistsResponse(false, null, null);
    }

    private void ensureDefaultPlaylists(UUID userId) {
        // Names per your examples; playlists.type is "DEFAULT" and they are public.
        playlistRepository.insertPlaylistIfMissing(
                userId, "DEFAULT_LIKES", "Default liked songs playlist", null, "DEFAULT", true);
        playlistRepository.insertPlaylistIfMissing(
                userId, "DEFAULT_DISLIKES", "Default disliked songs playlist", null, "DEFAULT", true);
    }

    private static String resolveNickname(String preferred, String email) {
        String candidate = trimToNull(preferred);
        if (candidate == null) {
            candidate = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        }
        if (candidate.length() > MAX_NICKNAME_LENGTH) {
            candidate = candidate.substring(0, MAX_NICKNAME_LENGTH);
        }
        return candidate.isBlank() ? "user" : candidate;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
