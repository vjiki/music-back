package com.vjiki.music.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.service.UserService;

@WebMvcTest(UserController.class)
@DisplayName("UserController integration tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Nested
    @DisplayName("GET /api/v1/users/{userId}")
    class GetUserById {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUserWhenFound() throws Exception {
            UUID userId = UUID.randomUUID();
            UserResponse userResponse = UserResponse.builder()
                    .id(userId)
                    .email("test@example.com")
                    .nickname("testuser")
                    .avatarUrl(null)
                    .accessLevel("USER")
                    .isActive(true)
                    .isVerified(false)
                    .lastLoginAt(null)
                    .createdAt(OffsetDateTime.now())
                    .build();

            given(userService.getUserById(userId)).willReturn(userResponse);

            mockMvc.perform(get("/api/v1/users/{userId}", userId).with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.nickname").value("testuser"));
        }

        @Test
        @DisplayName("should fail the request when user not found")
        void shouldFailWhenUserNotFound() throws Exception {
            UUID userId = UUID.randomUUID();
            given(userService.getUserById(userId)).willThrow(new RuntimeException("User not found"));

            // The application declares no exception handler, so the failure surfaces as a servlet
            // level error instead of a rendered 5xx response body.
            assertThatThrownBy(() -> mockMvc.perform(
                    get("/api/v1/users/{userId}", userId).with(user("test").roles("USER"))))
                    .rootCause()
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User not found");
        }
    }
}
