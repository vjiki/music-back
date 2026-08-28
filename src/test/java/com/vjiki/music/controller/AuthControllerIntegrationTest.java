package com.vjiki.music.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.User;
import com.vjiki.music.entity.UserRoleId;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.repository.UserRoleRepository;

@DisplayName("AuthController integration tests")
class AuthControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User saveLocalUser(String email, String rawPassword) {
        return userRepository.saveAndFlush(User.builder()
                .email(email)
                .nickname("testuser")
                .passwordHash(rawPassword == null ? null : passwordEncoder.encode(rawPassword))
                .accessLevel(AccessLevel.USER)
                .provider(AuthProvider.LOCAL)
                .isActive(true)
                .isVerified(false)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    @Nested
    @DisplayName("POST /api/v1/auth/authenticate")
    class Authenticate {

        @Test
        @DisplayName("should authenticate user with valid credentials")
        void shouldAuthenticateUserWithValidCredentials() throws Exception {
            String email = "authcontroller@example.com";
            String password = "password123";
            saveLocalUser(email, password);

            AuthRequest authRequest = new AuthRequest(email, password);

            mockMvc.perform(post("/api/v1/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())
                    .andExpect(jsonPath("$.message").value("Authentication successful"));
        }

        @Test
        @DisplayName("should return 401 with invalid credentials")
        void shouldReturnUnauthorizedWithInvalidCredentials() throws Exception {
            String email = "invalid@example.com";
            saveLocalUser(email, "correctpassword");

            AuthRequest authRequest = new AuthRequest(email, "wrongpassword");

            mockMvc.perform(post("/api/v1/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.authenticated").value(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class Register {

        @Test
        @DisplayName("should create user if not exists, create default USER role + default playlists, and be idempotent")
        void shouldRegisterIdempotentlyWithRoleAndDefaultPlaylists() throws Exception {
            String email = "new-user@example.com";
            String json = """
                    {
                      "email": "%s",
                      "password": "secret123",
                      "nickname": "New User"
                    }
                    """.formatted(email);

            MvcResult first = mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())
                    .andReturn();

            String userId1 = readUserId(first);
            assertThat(userId1).isNotNull();

            UUID userUuid = UUID.fromString(userId1);
            assertThat(userRoleRepository.existsById(new UserRoleId(userUuid, "USER"))).isTrue();

            List<Playlist> playlists = playlistRepository.findByUserId(userUuid);
            assertThat(playlists)
                    .extracting(Playlist::getName)
                    .containsExactlyInAnyOrder("DEFAULT_LIKES", "DEFAULT_DISLIKES");
            assertThat(playlists).allMatch(playlist -> "DEFAULT".equals(playlist.getType()));
            assertThat(playlists).allMatch(Playlist::getIsPublic);

            MvcResult second = mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())
                    .andReturn();

            assertThat(readUserId(second)).isEqualTo(userId1);
        }

        @Test
        @DisplayName("should allow registering without password")
        void shouldAllowRegisteringWithoutPassword() throws Exception {
            String email = "nopass@example.com";
            String json = """
                    { "email": "%s", "nickname": "No Pass" }
                    """.formatted(email);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists());

            User user = userRepository.findByEmail(email).orElseThrow();
            assertThat(user.getPasswordHash()).isNull();
        }

        private String readUserId(MvcResult result) throws Exception {
            JsonNode userId = objectMapper.readTree(result.getResponse().getContentAsString()).get("userId");
            return userId == null || userId.isNull() ? null : userId.asText();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/exists")
    class Exists {

        @Test
        @DisplayName("should return exists=false for unknown email")
        void shouldReturnFalseForUnknownEmail() throws Exception {
            mockMvc.perform(get("/api/v1/auth/exists").param("email", "unknown@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exists").value(false))
                    .andExpect(jsonPath("$.userId").doesNotExist())
                    .andExpect(jsonPath("$.provider").doesNotExist());
        }

        @Test
        @DisplayName("should return exists=false when provider filter does not match")
        void shouldReturnFalseWhenProviderFilterDoesNotMatch() throws Exception {
            String email = "local@example.com";

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"%s\", \"password\": \"secret123\" }".formatted(email)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(true));

            mockMvc.perform(get("/api/v1/auth/exists")
                            .param("email", email)
                            .param("provider", "GOOGLE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exists").value(false));
        }
    }
}
