package com.vjiki.music.controller

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(AuthController::class)
@ContextConfiguration(classes = [AuthControllerIntegrationTest.TestConfig::class])
class AuthControllerIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) : DescribeSpec({

    val userService = mockk<UserService>()

    describe("POST /api/v1/auth/authenticate") {
        it("should return 200 for successful authentication") {
            val userId = UUID.randomUUID()
            val authRequest = AuthRequest("test@example.com", "password123")
            val authResponse = AuthResponse(true, userId, "Authentication successful")

            every { userService.authenticate(authRequest) } returns authResponse

            mockMvc.perform(
                post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
        }

        it("should return 401 for failed authentication") {
            val authRequest = AuthRequest("test@example.com", "wrongpassword")
            val authResponse = AuthResponse(false, null, "Invalid email or password")

            every { userService.authenticate(authRequest) } returns authResponse

            mockMvc.perform(
                post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest))
            )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.authenticated").value(false))
        }
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun userService(): UserService = mockk()
    }
})

