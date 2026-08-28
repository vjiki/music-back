package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.support.AbstractIntegrationTest;

class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User createTestUser(String email, String nickname) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .accessLevel(AccessLevel.USER)
                        .provider(AuthProvider.LOCAL)
                        .isActive(true)
                        .isVerified(false)
                        .createdBy("system")
                        .modifiedBy("system")
                        .build());
    }

    private User createTestUserWithPassword(String email, String nickname, String password) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .passwordHash(passwordEncoder.encode(password))
                        .accessLevel(AccessLevel.USER)
                        .provider(AuthProvider.LOCAL)
                        .isActive(true)
                        .isVerified(false)
                        .createdBy("system")
                        .modifiedBy("system")
                        .build());
    }

    @Nested
    @DisplayName("UserService Integration Tests")
    class UserServiceIntegrationTests {

        @Test
        @DisplayName("should get user by id")
        void shouldGetUserById() {
            User user = createTestUser("getuser@example.com", "getuser");

            UserResponse result = userService.getUserById(user.getId());

            assertThat(result.getId()).isEqualTo(user.getId());
            assertThat(result.getEmail()).isEqualTo("getuser@example.com");
            assertThat(result.getNickname()).isEqualTo("getuser");
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            Throwable exception = catchThrowable(() -> userService.getUserById(UUID.randomUUID()));

            assertThat(exception).isNotNull();
            assertThat(exception).hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should authenticate user with valid credentials")
        void shouldAuthenticateUserWithValidCredentials() {
            String password = "password123";
            User user = createTestUserWithPassword("auth@example.com", "authuser", password);

            AuthResponse result = userService.authenticate(new AuthRequest("auth@example.com", password));

            assertThat(result.getAuthenticated()).isTrue();
            assertThat(result.getUserId()).isEqualTo(user.getId());
            assertThat(result.getMessage()).isEqualTo("Authentication successful");
        }

        @Test
        @DisplayName("should fail authentication with invalid password")
        void shouldFailAuthenticationWithInvalidPassword() {
            createTestUserWithPassword("invalid@example.com", "invaliduser", "correctpassword");

            AuthResponse result = userService.authenticate(new AuthRequest("invalid@example.com", "wrongpassword"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getMessage()).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("should fail authentication for inactive user")
        void shouldFailAuthenticationForInactiveUser() {
            String password = "password123";
            User user = createTestUserWithPassword("inactive@example.com", "inactiveuser", password);
            user.setIsActive(false);
            userRepository.save(user);

            AuthResponse result = userService.authenticate(new AuthRequest("inactive@example.com", password));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getMessage()).isEqualTo("User account is inactive");
        }

        @Test
        @DisplayName("should fail authentication for non-existent user")
        void shouldFailAuthenticationForNonExistentUser() {
            AuthResponse result = userService.authenticate(new AuthRequest("nonexistent@example.com", "password"));

            assertThat(result.getAuthenticated()).isFalse();
            assertThat(result.getMessage()).isEqualTo("Invalid email or password");
        }
    }
}
