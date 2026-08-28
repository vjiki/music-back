package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.firebase.auth.FirebaseAuth;
import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.repository.UserRoleRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectProvider<FirebaseAuth> firebaseAuthProvider;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = newService(true);
    }

    private UserServiceImpl newService(boolean localAuthEnabled) {
        return new UserServiceImpl(
                userRepository,
                userRoleRepository,
                playlistRepository,
                passwordEncoder,
                firebaseAuthProvider,
                localAuthEnabled);
    }

    private User.UserBuilder localUser(UUID userId, String email) {
        return User.builder()
                .id(userId)
                .email(email)
                .passwordHash("hashedPassword")
                .provider(AuthProvider.LOCAL)
                .nickname("testuser")
                .accessLevel(AccessLevel.USER)
                .isActive(true)
                .isVerified(false)
                .createdBy("system")
                .modifiedBy("system");
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUserWhenFound() {
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(localUser(userId, "test@example.com").build()));

            UserResponse result = userService.getUserById(userId);

            assertThat(result.getId()).isEqualTo(userId);
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getNickname()).isEqualTo("testuser");
            assertThat(result.getAccessLevel()).isEqualTo(AccessLevel.USER.name());
            assertThat(result.getIsActive()).isTrue();
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(userId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User not found with id: " + userId);
        }
    }

    @Nested
    @DisplayName("authenticate")
    class Authenticate {

        @Test
        @DisplayName("should return success for valid credentials")
        void shouldReturnSuccessForValidCredentials() {
            UUID userId = UUID.randomUUID();
            String email = "test@example.com";

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(localUser(userId, email).build()));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

            AuthResponse result = userService.authenticate(new AuthRequest(email, "password123"));

            assertThat(result.getAuthenticated()).isTrue();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getMessage()).isEqualTo("Authentication successful");
        }

        @Test
        @DisplayName("should return failure for invalid password")
        void shouldReturnFailureForInvalidPassword() {
            UUID userId = UUID.randomUUID();
            String email = "test@example.com";

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(localUser(userId, email).build()));
            when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

            AuthResponse result = userService.authenticate(new AuthRequest(email, "wrongPassword"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getUserId()).isNull();
            assertThat(result.getMessage()).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("should return failure for inactive user")
        void shouldReturnFailureForInactiveUser() {
            UUID userId = UUID.randomUUID();
            String email = "test@example.com";

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(localUser(userId, email).isActive(false).build()));

            AuthResponse result = userService.authenticate(new AuthRequest(email, "password123"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getUserId()).isNull();
            assertThat(result.getMessage()).isEqualTo("User account is inactive");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("should return failure for non-existent user")
        void shouldReturnFailureForNonExistentUser() {
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            AuthResponse result = userService.authenticate(new AuthRequest(email, "password123"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getMessage()).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("should return failure when a local user has no password set")
        void shouldReturnFailureWhenPasswordNotSet() {
            UUID userId = UUID.randomUUID();
            String email = "test@example.com";

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(localUser(userId, email).passwordHash(null).build()));

            AuthResponse result = userService.authenticate(new AuthRequest(email, "password123"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getMessage()).isEqualTo("Password not set for this user");
        }

        @Test
        @DisplayName("should return failure for an OAuth account")
        void shouldReturnFailureForOAuthAccount() {
            UUID userId = UUID.randomUUID();
            String email = "test@example.com";

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(localUser(userId, email).provider(AuthProvider.GOOGLE).build()));

            AuthResponse result = userService.authenticate(new AuthRequest(email, "password123"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getMessage()).isEqualTo("This account uses OAuth authentication");
        }

        @Test
        @DisplayName("should reject local authentication when it is disabled")
        void shouldRejectWhenLocalAuthDisabled() {
            AuthResponse result = newService(false)
                    .authenticate(new AuthRequest("test@example.com", "password123"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getUserId()).isNull();
            assertThat(result.getMessage()).isEqualTo("Local authentication is disabled");
            verify(userRepository, never()).findByEmail(any());
            verifyNoInteractions(passwordEncoder);
        }
    }
}
